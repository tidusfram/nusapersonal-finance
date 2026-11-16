// NusaPersonal Finance — Application Logic & Store
(function () {
  'use strict';

  // Helper formatting currency
  const formatRupiah = (number) => {
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(number || 0);
  };

  const formatNumber = (num) => {
    return new Intl.NumberFormat('id-ID').format(num || 0);
  };

  // State
  const STORAGE_KEY = 'nusafinance_db_v1';
  let db = null;
  let activeTab = 'dashboard';
  let activePeriod = '2026-11'; // YYYY-MM
  let categoryChartInstance = null;
  let cashflowChartInstance = null;

  // Initialize DB from LocalStorage or INITIAL_DATA
  function loadDatabase() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        db = JSON.parse(raw);
      } else {
        resetToDefaultData(false);
      }
    } catch (e) {
      console.error('Error loading DB:', e);
      resetToDefaultData(false);
    }
  }

  function saveDatabase() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(db));
    } catch (e) {
      console.error('Error saving DB:', e);
      alert('Gagal menyimpan perubahan ke memori perangkat.');
    }
  }

  function resetToDefaultData(askConfirm = true) {
    if (askConfirm) {
      if (!confirm('Apakah Anda yakin ingin memulihkan seluruh data contoh simulasi awal? Semua data input baru akan ditimpa.')) {
        return;
      }
    }
    db = JSON.parse(JSON.stringify(INITIAL_DATA));
    saveDatabase();
    renderAll();
    showToast('Data simulasi berhasil dimuat ulang!');
  }

  // Toast notification
  function showToast(msg) {
    let toast = document.getElementById('toast');
    if (!toast) {
      toast = document.createElement('div');
      toast.id = 'toast';
      toast.className = 'fixed top-4 left-1/2 -translate-x-1/2 z-50 bg-slate-900/90 text-white border border-emerald-500/50 px-4 py-2.5 rounded-full shadow-2xl flex items-center gap-2 text-sm font-medium backdrop-blur transition-all duration-300 pointer-events-none opacity-0';
      document.body.appendChild(toast);
    }
    toast.innerHTML = `<span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>${msg}`;
    toast.classList.remove('opacity-0', '-translate-y-4');
    toast.classList.add('opacity-100', 'translate-y-0');
    setTimeout(() => {
      toast.classList.remove('opacity-100', 'translate-y-0');
      toast.classList.add('opacity-0', '-translate-y-4');
    }, 2800);
  }

  // Calculations
  function calculateAccountBalances() {
    const balances = {};
    const ins = {};
    const outs = {};

    db.accounts.forEach(acc => {
      balances[acc.id] = acc.initialBalance || 0;
      ins[acc.id] = 0;
      outs[acc.id] = 0;
    });

    db.transactions.forEach(tx => {
      if (tx.status !== 'Posted') return;
      const amt = Number(tx.amount) || 0;

      if (tx.type === 'Income') {
        if (balances[tx.accountId] !== undefined) {
          balances[tx.accountId] += amt;
          ins[tx.accountId] += amt;
        }
      } else if (tx.type === 'Expense' || tx.type === 'Debt Payment' || tx.type === 'Investment') {
        if (balances[tx.accountId] !== undefined) {
          balances[tx.accountId] -= amt;
          outs[tx.accountId] += amt;
        }
      } else if (tx.type === 'Savings') {
        // Tabungan masuk ke akun tabungan atau keluar dari akun sumber
        if (balances[tx.accountId] !== undefined) {
          balances[tx.accountId] += amt;
          ins[tx.accountId] += amt;
        }
      } else if (tx.type === 'Transfer') {
        // Transfer Out jika nominal negatif atau bila transferId dipasangkan
        if (balances[tx.accountId] !== undefined) {
          balances[tx.accountId] -= amt;
          outs[tx.accountId] += amt;
        }
      }
    });

    return { balances, ins, outs };
  }

  function getPeriodSummary(period) {
    let income = 0;
    let expense = 0;
    let debtPayment = 0;
    let savings = 0;
    let investment = 0;
    const expenseByCategory = {};

    db.transactions.forEach(tx => {
      if (tx.status !== 'Posted') return;
      if (period && !tx.date.startsWith(period)) return;

      const amt = Number(tx.amount) || 0;
      if (tx.type === 'Income') {
        income += amt;
      } else if (tx.type === 'Expense') {
        expense += amt;
        const cat = tx.category || 'Lainnya';
        expenseByCategory[cat] = (expenseByCategory[cat] || 0) + amt;
      } else if (tx.type === 'Debt Payment') {
        debtPayment += amt;
      } else if (tx.type === 'Savings') {
        savings += amt;
      } else if (tx.type === 'Investment') {
        investment += amt;
      }
    });

    const netCashflow = income - expense;
    const availableCashflow = income - (expense + debtPayment + savings + investment);
    const savingRate = income > 0 ? (((savings + investment) / income) * 100).toFixed(1) : 0;

    return {
      income,
      expense,
      debtPayment,
      savings,
      investment,
      netCashflow,
      availableCashflow,
      savingRate,
      expenseByCategory
    };
  }

  function getTotalAssets() {
    return db.assets.reduce((sum, item) => sum + (Number(item.currentValue) || 0), 0);
  }

  function getTotalInvestments() {
    return db.investments.reduce((sum, item) => sum + (Number(item.currentValue) || 0), 0);
  }

  function getTotalInvestmentCost() {
    return db.investments.reduce((sum, item) => {
      const units = Number(item.units) || 0;
      const price = Number(item.purchasePricePerUnit) || 0;
      return sum + (units * price);
    }, 0);
  }

  function getTotalDebts() {
    return db.debts.reduce((sum, item) => sum + (Number(item.remainingAmount) || 0), 0);
  }

  function getNetWorth() {
    const { balances } = calculateAccountBalances();
    const totalCash = Object.values(balances).reduce((sum, val) => sum + val, 0);
    const totalAssets = getTotalAssets();
    const totalInvestments = getTotalInvestments();
    const totalDebts = getTotalDebts();

    const netWorth = (totalCash + totalAssets + totalInvestments) - totalDebts;
    return {
      totalCash,
      totalAssets,
      totalInvestments,
      totalDebts,
      netWorth
    };
  }

  // Render Screens
  function renderAll() {
    renderPeriodSelector();
    renderDashboard();
    renderTransactions();
    renderBudgets();
    renderGoals();
    renderAssetsAndDebts();
    renderReports();
    renderWorkflowsAndGlossary();
  }

  function renderPeriodSelector() {
    const selector = document.getElementById('period-selector');
    if (!selector) return;

    // Collect available months from transactions or default
    const months = new Set(['2026-11', '2026-10', '2026-12']);
    db.transactions.forEach(tx => {
      if (tx.date && tx.date.length >= 7) {
        months.add(tx.date.substring(0, 7));
      }
    });

    const sorted = Array.from(months).sort().reverse();
    selector.innerHTML = sorted.map(m => {
      const [year, month] = m.split('-');
      const d = new Date(year, parseInt(month) - 1, 1);
      const label = d.toLocaleDateString('id-ID', { month: 'long', year: 'numeric' });
      return `<option value="${m}" ${m === activePeriod ? 'selected' : ''}>${label}</option>`;
    }).join('');
  }

  function renderDashboard() {
    const summary = getPeriodSummary(activePeriod);
    const net = getNetWorth();

    // Stats cards
    const elIncome = document.getElementById('dash-income');
    const elExpense = document.getElementById('dash-expense');
    const elCashflow = document.getElementById('dash-cashflow');
    const elTotalCash = document.getElementById('dash-total-cash');
    const elTotalDebt = document.getElementById('dash-total-debt');
    const elNetWorth = document.getElementById('dash-net-worth');
    const elSavingRate = document.getElementById('dash-saving-rate');

    if (elIncome) elIncome.textContent = formatRupiah(summary.income);
    if (elExpense) elExpense.textContent = formatRupiah(summary.expense);
    if (elCashflow) {
      elCashflow.textContent = formatRupiah(summary.netCashflow);
      elCashflow.className = summary.netCashflow >= 0 
        ? 'text-lg font-bold text-emerald-400' 
        : 'text-lg font-bold text-rose-400';
    }
    if (elTotalCash) elTotalCash.textContent = formatRupiah(net.totalCash);
    if (elTotalDebt) elTotalDebt.textContent = formatRupiah(net.totalDebts);
    if (elNetWorth) {
      elNetWorth.textContent = formatRupiah(net.netWorth);
      elNetWorth.className = net.netWorth >= 0 
        ? 'text-xl font-black text-emerald-400' 
        : 'text-xl font-black text-rose-400';
    }
    if (elSavingRate) elSavingRate.textContent = `${summary.savingRate}%`;

    // Render Quick Accounts preview in Dashboard
    const { balances } = calculateAccountBalances();
    const accContainer = document.getElementById('dash-accounts-list');
    if (accContainer) {
      accContainer.innerHTML = db.accounts.map(acc => {
        const bal = balances[acc.id] || 0;
        let badgeColor = 'bg-blue-500/20 text-blue-300 border-blue-500/30';
        if (acc.type === 'Cash') badgeColor = 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30';
        if (acc.type === 'E-Wallet') badgeColor = 'bg-purple-500/20 text-purple-300 border-purple-500/30';
        if (acc.type === 'Savings') badgeColor = 'bg-amber-500/20 text-amber-300 border-amber-500/30';

        return `
          <div class="flex items-center justify-between p-3 rounded-xl bg-slate-800/80 border border-slate-700/60 shadow-sm">
            <div class="flex items-center gap-3">
              <span class="px-2 py-0.5 text-xs font-semibold rounded border ${badgeColor}">${acc.type}</span>
              <div>
                <div class="font-medium text-slate-200 text-sm">${acc.name}</div>
                <div class="text-[11px] text-slate-400">${acc.id}</div>
              </div>
            </div>
            <div class="text-right">
              <div class="font-semibold text-sm ${bal >= 0 ? 'text-slate-100' : 'text-rose-400'}">${formatRupiah(bal)}</div>
            </div>
          </div>
        `;
      }).join('');
    }

    // Render Recent Transactions
    const recentContainer = document.getElementById('dash-recent-tx');
    if (recentContainer) {
      const recentTx = [...db.transactions]
        .sort((a, b) => new Date(b.date) - new Date(a.date))
        .slice(0, 4);

      if (recentTx.length === 0) {
        recentContainer.innerHTML = `<div class="text-xs text-slate-400 py-3 text-center">Belum ada transaksi.</div>`;
      } else {
        recentContainer.innerHTML = recentTx.map(tx => renderTxItemHtml(tx)).join('');
      }
    }

    // Render Chart
    renderExpenseCategoryChart(summary.expenseByCategory);
  }

  function renderExpenseCategoryChart(expenseByCategory) {
    const canvas = document.getElementById('category-chart');
    if (!canvas) return;

    const labels = Object.keys(expenseByCategory);
    const dataValues = Object.values(expenseByCategory);

    if (categoryChartInstance) {
      categoryChartInstance.destroy();
    }

    if (labels.length === 0) {
      const ctx = canvas.getContext('2d');
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.font = '12px sans-serif';
      ctx.fillStyle = '#94a3b8';
      ctx.textAlign = 'center';
      ctx.fillText('Tidak ada pengeluaran pada periode ini', canvas.width / 2, canvas.height / 2);
      return;
    }

    categoryChartInstance = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: dataValues,
          backgroundColor: [
            '#10b981', '#3b82f6', '#f59e0b', '#ec4899', '#8b5cf6',
            '#14b8a6', '#f97316', '#64748b', '#06b6d4', '#e11d48'
          ],
          borderWidth: 2,
          borderColor: '#1e293b'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              boxWidth: 10,
              color: '#cbd5e1',
              font: { size: 11 }
            }
          },
          tooltip: {
            callbacks: {
              label: function (ctx) {
                return ` ${ctx.label}: ${formatRupiah(ctx.parsed)}`;
              }
            }
          }
        },
        cutout: '68%'
      }
    });
  }

  // Transactions Screen
  function renderTxItemHtml(tx) {
    let typeBadge = '';
    let nominalClass = '';
    let sign = '-';

    if (tx.type === 'Income') {
      typeBadge = 'bg-emerald-500/20 text-emerald-400 border-emerald-500/30';
      nominalClass = 'text-emerald-400 font-bold';
      sign = '+';
    } else if (tx.type === 'Expense') {
      typeBadge = 'bg-rose-500/20 text-rose-400 border-rose-500/30';
      nominalClass = 'text-rose-400 font-bold';
      sign = '-';
    } else if (tx.type === 'Savings') {
      typeBadge = 'bg-blue-500/20 text-blue-400 border-blue-500/30';
      nominalClass = 'text-blue-400 font-bold';
      sign = '→';
    } else if (tx.type === 'Debt Payment') {
      typeBadge = 'bg-amber-500/20 text-amber-400 border-amber-500/30';
      nominalClass = 'text-amber-400 font-bold';
      sign = '-';
    } else if (tx.type === 'Investment') {
      typeBadge = 'bg-purple-500/20 text-purple-400 border-purple-500/30';
      nominalClass = 'text-purple-400 font-bold';
      sign = '-';
    } else {
      typeBadge = 'bg-slate-500/20 text-slate-300 border-slate-500/30';
      nominalClass = 'text-slate-200 font-semibold';
      sign = '↔';
    }

    const acc = db.accounts.find(a => a.id === tx.accountId);
    const accName = acc ? acc.name : tx.accountId;

    return `
      <div class="flex items-center justify-between p-3.5 bg-slate-800/80 rounded-xl border border-slate-700/60 shadow-sm transition hover:border-slate-600">
        <div class="flex items-start gap-3 flex-1 min-w-0 pr-2">
          <div class="flex flex-col items-center justify-center w-10 h-10 rounded-xl bg-slate-900 border border-slate-700/70 shrink-0">
            <span class="text-xs font-bold text-slate-300">${tx.date.substring(8, 10)}</span>
            <span class="text-[9px] uppercase tracking-wider text-slate-400">${new Date(tx.date).toLocaleDateString('id-ID', { month: 'short' })}</span>
          </div>
          <div class="min-w-0 flex-1">
            <div class="flex items-center gap-1.5 flex-wrap">
              <span class="text-[10px] px-1.5 py-0.2 rounded border ${typeBadge}">${tx.type}</span>
              <span class="text-xs font-semibold text-slate-300 truncate">${tx.category}</span>
            </div>
            <div class="text-sm font-medium text-slate-100 truncate mt-0.5">${tx.desc || tx.category}</div>
            <div class="text-[11px] text-slate-400 flex items-center gap-2 mt-0.5">
              <span>💳 ${accName}</span>
              ${tx.party ? `<span>• ${tx.party}</span>` : ''}
              ${tx.status !== 'Posted' ? `<span class="text-amber-400">(${tx.status})</span>` : ''}
            </div>
          </div>
        </div>
        <div class="text-right shrink-0">
          <div class="text-sm ${nominalClass}">${sign} ${formatRupiah(tx.amount)}</div>
          <button onclick="window.nusaApp.deleteTransaction('${tx.id}')" class="text-slate-400 hover:text-rose-400 text-xs mt-1 p-1" title="Hapus Transaksi">
            🗑️
          </button>
        </div>
      </div>
    `;
  }

  function renderTransactions() {
    const listContainer = document.getElementById('tx-full-list');
    if (!listContainer) return;

    const typeFilter = document.getElementById('tx-filter-type')?.value || 'ALL';
    const searchQuery = (document.getElementById('tx-search')?.value || '').toLowerCase();

    let filtered = [...db.transactions].filter(tx => {
      if (activePeriod && !tx.date.startsWith(activePeriod)) return false;
      if (typeFilter !== 'ALL' && tx.type !== typeFilter) return false;
      if (searchQuery) {
        const matchDesc = (tx.desc || '').toLowerCase().includes(searchQuery);
        const matchCat = (tx.category || '').toLowerCase().includes(searchQuery);
        const matchParty = (tx.party || '').toLowerCase().includes(searchQuery);
        if (!matchDesc && !matchCat && !matchParty) return false;
      }
      return true;
    });

    filtered.sort((a, b) => new Date(b.date) - new Date(a.date));

    if (filtered.length === 0) {
      listContainer.innerHTML = `
        <div class="text-center py-10 px-4 bg-slate-800/40 rounded-2xl border border-dashed border-slate-700">
          <div class="text-3xl mb-2">💸</div>
          <div class="text-sm font-medium text-slate-300">Tidak ada transaksi ditemukan</div>
          <div class="text-xs text-slate-400 mt-1">Coba sesuaikan filter atau tekan tombol + di bawah untuk mencatat transaksi baru.</div>
        </div>
      `;
      return;
    }

    listContainer.innerHTML = filtered.map(tx => renderTxItemHtml(tx)).join('');
  }

  // Budgets Screen
  function renderBudgets() {
    const budgetContainer = document.getElementById('budget-list');
    if (!budgetContainer) return;

    const summary = getPeriodSummary(activePeriod);
    const expenseByCat = summary.expenseByCategory;

    // Filter or list budgets for this period
    let budgetsForPeriod = db.budgets.filter(b => !b.period || b.period === activePeriod);
    if (budgetsForPeriod.length === 0) {
      budgetsForPeriod = db.budgets;
    }

    let totalBudgeted = 0;
    let totalRealized = 0;

    const cardsHtml = budgetsForPeriod.map(b => {
      const real = expenseByCat[b.category] || 0;
      const budgetAmt = Number(b.amount) || 0;
      const remaining = budgetAmt - real;
      const pct = budgetAmt > 0 ? Math.min(Math.round((real / budgetAmt) * 100), 999) : 0;

      totalBudgeted += budgetAmt;
      totalRealized += real;

      let statusColor = 'bg-emerald-500';
      let statusBadge = 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30';
      let statusText = 'Aman';

      if (pct >= 100) {
        statusColor = 'bg-rose-500';
        statusBadge = 'bg-rose-500/20 text-rose-300 border-rose-500/30';
        statusText = 'Overbudget!';
      } else if (pct >= 80) {
        statusColor = 'bg-amber-500';
        statusBadge = 'bg-amber-500/20 text-amber-300 border-amber-500/30';
        statusText = 'Waspada (>80%)';
      }

      return `
        <div class="p-4 bg-slate-800/80 rounded-2xl border border-slate-700/60 shadow-sm space-y-2.5">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="font-bold text-slate-200 text-sm">${b.category}</span>
              <span class="text-[10px] px-1.5 py-0.5 rounded border ${statusBadge}">${statusText}</span>
            </div>
            <div class="text-right">
              <span class="text-xs font-semibold ${pct >= 100 ? 'text-rose-400' : 'text-slate-300'}">${pct}%</span>
            </div>
          </div>

          <!-- Progress Bar -->
          <div class="w-full bg-slate-900 rounded-full h-2.5 overflow-hidden p-0.5 border border-slate-700">
            <div class="${statusColor} h-1.5 rounded-full transition-all duration-500" style="width: ${Math.min(pct, 100)}%"></div>
          </div>

          <div class="flex items-center justify-between text-xs text-slate-400 pt-1 border-t border-slate-700/40">
            <div>
              <span>Realisasi: </span>
              <strong class="text-slate-200">${formatRupiah(real)}</strong>
            </div>
            <div>
              <span>Plafon: </span>
              <strong class="text-slate-300">${formatRupiah(budgetAmt)}</strong>
            </div>
            <div>
              <span>Sisa: </span>
              <strong class="${remaining < 0 ? 'text-rose-400' : 'text-emerald-400'}">${formatRupiah(remaining)}</strong>
            </div>
          </div>
        </div>
      `;
    }).join('');

    const totalPct = totalBudgeted > 0 ? Math.round((totalRealized / totalBudgeted) * 100) : 0;
    const headerSummaryHtml = `
      <div class="p-4 rounded-2xl bg-gradient-to-br from-indigo-950/60 to-slate-900 border border-indigo-700/40 shadow-lg mb-4">
        <div class="flex items-center justify-between mb-2">
          <span class="text-xs text-indigo-300 uppercase tracking-wider font-semibold">Total Anggaran Periode</span>
          <span class="text-xs font-bold px-2 py-0.5 rounded-full bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">${totalPct}% Terpakai</span>
        </div>
        <div class="flex items-baseline justify-between">
          <div class="text-xl font-black text-white">${formatRupiah(totalRealized)}</div>
          <div class="text-xs text-slate-400">dari plafon ${formatRupiah(totalBudgeted)}</div>
        </div>
        <div class="w-full bg-slate-950 rounded-full h-2 mt-3 overflow-hidden border border-slate-800">
          <div class="${totalPct >= 100 ? 'bg-rose-500' : totalPct >= 80 ? 'bg-amber-500' : 'bg-indigo-500'} h-full rounded-full transition-all" style="width: ${Math.min(totalPct, 100)}%"></div>
        </div>
      </div>
    `;

    budgetContainer.innerHTML = headerSummaryHtml + cardsHtml;
  }

  // Goals Screen
  function renderGoals() {
    const goalsContainer = document.getElementById('goals-list');
    if (!goalsContainer) return;

    if (db.goals.length === 0) {
      goalsContainer.innerHTML = `<div class="text-xs text-slate-400 py-6 text-center">Belum ada target tabungan dibuat.</div>`;
      return;
    }

    goalsContainer.innerHTML = db.goals.map(g => {
      const target = Number(g.targetAmount) || 0;
      const current = Number(g.currentAmount) || 0;
      const remaining = Math.max(0, target - current);
      const pct = target > 0 ? Math.min(Math.round((current / target) * 100), 100) : 0;

      let priorityBadge = 'bg-slate-700 text-slate-300';
      if (g.priority === 'Tinggi') priorityBadge = 'bg-rose-500/20 text-rose-300 border border-rose-500/30';
      if (g.priority === 'Sedang') priorityBadge = 'bg-amber-500/20 text-amber-300 border border-amber-500/30';
      if (g.priority === 'Rendah') priorityBadge = 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30';

      return `
        <div class="p-4 bg-slate-800/80 rounded-2xl border border-slate-700/60 shadow-sm space-y-3">
          <div class="flex items-start justify-between">
            <div>
              <div class="flex items-center gap-2">
                <span class="text-[10px] px-1.5 py-0.5 rounded ${priorityBadge}">Prioritas ${g.priority}</span>
                <span class="text-[11px] text-slate-400">📅 ${g.dueDate || 'Tanpa batas'}</span>
              </div>
              <h4 class="font-bold text-slate-100 text-base mt-1">${g.name}</h4>
              <p class="text-xs text-slate-400">${g.notes || ''}</p>
            </div>
            <div class="text-right">
              <span class="text-base font-extrabold text-emerald-400">${pct}%</span>
            </div>
          </div>

          <!-- Progress -->
          <div class="w-full bg-slate-900 rounded-full h-2.5 overflow-hidden border border-slate-700">
            <div class="bg-gradient-to-r from-emerald-500 to-teal-400 h-full rounded-full transition-all duration-500" style="width: ${pct}%"></div>
          </div>

          <div class="flex items-center justify-between text-xs text-slate-300 pt-1">
            <div>
              <span class="text-slate-400">Terkumpul: </span>
              <strong class="text-emerald-400">${formatRupiah(current)}</strong>
            </div>
            <div>
              <span class="text-slate-400">Target: </span>
              <strong>${formatRupiah(target)}</strong>
            </div>
          </div>

          <div class="pt-2 flex items-center justify-between border-t border-slate-700/40">
            <span class="text-[11px] text-slate-400">Sisa: <strong class="text-slate-200">${formatRupiah(remaining)}</strong></span>
            <button onclick="window.nusaApp.openDepositGoalModal('${g.id}')" class="px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-lg shadow-sm">
              + Tambah Dana
            </button>
          </div>
        </div>
      `;
    }).join('');
  }

  // Assets & Debts Screen
  function renderAssetsAndDebts() {
    // Debts
    const debtsContainer = document.getElementById('debts-list');
    if (debtsContainer) {
      if (db.debts.length === 0) {
        debtsContainer.innerHTML = `<div class="text-xs text-slate-400 py-4 text-center">Bebas utang! Tidak ada catatan kewajiban.</div>`;
      } else {
        debtsContainer.innerHTML = db.debts.map(d => {
          const init = Number(d.initialAmount) || 0;
          const rem = Number(d.remainingAmount) || 0;
          const paid = Math.max(0, init - rem);
          const pct = init > 0 ? Math.min(Math.round((paid / init) * 100), 100) : 0;

          return `
            <div class="p-4 bg-slate-800/80 rounded-2xl border border-slate-700/60 shadow-sm space-y-2.5">
              <div class="flex items-start justify-between">
                <div>
                  <div class="flex items-center gap-1.5">
                    <span class="text-[10px] px-1.5 py-0.5 rounded bg-rose-500/20 text-rose-300 border border-rose-500/30">${d.type}</span>
                    <span class="text-xs font-semibold text-slate-300">${d.lender}</span>
                  </div>
                  <h4 class="font-bold text-slate-100 text-sm mt-1">Sisa: <span class="text-rose-400 font-extrabold">${formatRupiah(rem)}</span></h4>
                </div>
                <div class="text-right">
                  <span class="text-xs text-slate-400">Cicilan/Bulan</span>
                  <div class="text-xs font-bold text-amber-300">${formatRupiah(d.monthlyInstallment)}</div>
                </div>
              </div>

              <!-- Progress Pelunasan -->
              <div class="space-y-1">
                <div class="flex justify-between text-[11px] text-slate-400">
                  <span>Terbayar: ${formatRupiah(paid)} (${pct}%)</span>
                  <span>Awal: ${formatRupiah(init)}</span>
                </div>
                <div class="w-full bg-slate-900 rounded-full h-2 overflow-hidden border border-slate-700">
                  <div class="bg-amber-500 h-full rounded-full transition-all" style="width: ${pct}%"></div>
                </div>
              </div>

              <div class="flex items-center justify-between text-[11px] text-slate-400 pt-1 border-t border-slate-700/40">
                <span>Jatuh Tempo: <strong>${d.dueDate}</strong></span>
                <span class="truncate max-w-[150px] text-slate-400">${d.notes || ''}</span>
              </div>
            </div>
          `;
        }).join('');
      }
    }

    // Physical Assets
    const assetsContainer = document.getElementById('assets-list');
    if (assetsContainer) {
      if (db.assets.length === 0) {
        assetsContainer.innerHTML = `<div class="text-xs text-slate-400 py-4 text-center">Belum ada aset fisik dicatat.</div>`;
      } else {
        assetsContainer.innerHTML = db.assets.map(a => {
          const buy = Number(a.purchasePrice) || 0;
          const curr = Number(a.currentValue) || 0;
          const diff = curr - buy;
          const diffPct = buy > 0 ? ((diff / buy) * 100).toFixed(1) : 0;

          return `
            <div class="p-3.5 bg-slate-800/80 rounded-xl border border-slate-700/60 flex items-center justify-between">
              <div>
                <div class="flex items-center gap-1.5">
                  <span class="text-[10px] px-1.5 py-0.5 rounded bg-blue-500/20 text-blue-300 border border-blue-500/30">${a.category}</span>
                  <span class="font-bold text-sm text-slate-200">${a.name}</span>
                </div>
                <div class="text-xs text-slate-400 mt-0.5">Beli: ${formatRupiah(buy)} • ${a.purchaseDate || ''}</div>
              </div>
              <div class="text-right">
                <div class="text-sm font-bold text-slate-100">${formatRupiah(curr)}</div>
                <div class="text-[11px] ${diff >= 0 ? 'text-emerald-400' : 'text-rose-400'}">
                  ${diff >= 0 ? '+' : ''}${formatRupiah(diff)} (${diffPct}%)
                </div>
              </div>
            </div>
          `;
        }).join('');
      }
    }

    // Investments
    const invContainer = document.getElementById('investments-list');
    if (invContainer) {
      if (db.investments.length === 0) {
        invContainer.innerHTML = `<div class="text-xs text-slate-400 py-4 text-center">Belum ada portofolio investasi dicatat.</div>`;
      } else {
        invContainer.innerHTML = db.investments.map(inv => {
          const units = Number(inv.units) || 0;
          const buyPrice = Number(inv.purchasePricePerUnit) || 0;
          const cost = units * buyPrice;
          const curr = Number(inv.currentValue) || 0;
          const gain = curr - cost;
          const gainPct = cost > 0 ? ((gain / cost) * 100).toFixed(1) : 0;

          return `
            <div class="p-4 bg-slate-800/80 rounded-2xl border border-slate-700/60 shadow-sm space-y-2">
              <div class="flex items-start justify-between">
                <div>
                  <div class="flex items-center gap-1.5">
                    <span class="text-[10px] px-1.5 py-0.5 rounded bg-purple-500/20 text-purple-300 border border-purple-500/30">${inv.instrument}</span>
                    <span class="font-bold text-slate-100 text-sm">${inv.code}</span>
                  </div>
                  <div class="text-[11px] text-slate-400 mt-1">
                    ${formatNumber(units)} unit @ ${formatRupiah(buyPrice)} (Modal: ${formatRupiah(cost)})
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-sm font-black text-slate-100">${formatRupiah(curr)}</div>
                  <div class="text-xs font-semibold ${gain >= 0 ? 'text-emerald-400' : 'text-rose-400'}">
                    ${gain >= 0 ? '+' : ''}${formatRupiah(gain)} (${gainPct}%)
                  </div>
                </div>
              </div>
              <div class="text-[10px] text-slate-400 pt-1 border-t border-slate-700/40">
                Catatan Risiko: <span class="text-slate-300">${inv.riskNote || 'Simulasi'}</span>
              </div>
            </div>
          `;
        }).join('');
      }
    }
  }

  // Reports & Net Worth Screen
  function renderReports() {
    const summary = getPeriodSummary(activePeriod);
    const net = getNetWorth();

    const elRepIncome = document.getElementById('rep-income');
    const elRepExpense = document.getElementById('rep-expense');
    const elRepDebt = document.getElementById('rep-debt-payment');
    const elRepSavings = document.getElementById('rep-savings');
    const elRepInvest = document.getElementById('rep-invest');
    const elRepAvailable = document.getElementById('rep-available');
    const elRepNetWorth = document.getElementById('rep-net-worth');
    const elRepCash = document.getElementById('rep-cash');
    const elRepAssets = document.getElementById('rep-assets');
    const elRepInvTotal = document.getElementById('rep-inv-total');
    const elRepDebtsTotal = document.getElementById('rep-debts-total');

    if (elRepIncome) elRepIncome.textContent = formatRupiah(summary.income);
    if (elRepExpense) elRepExpense.textContent = formatRupiah(summary.expense);
    if (elRepDebt) elRepDebt.textContent = formatRupiah(summary.debtPayment);
    if (elRepSavings) elRepSavings.textContent = formatRupiah(summary.savings);
    if (elRepInvest) elRepInvest.textContent = formatRupiah(summary.investment);
    if (elRepAvailable) elRepAvailable.textContent = formatRupiah(summary.availableCashflow);

    if (elRepNetWorth) elRepNetWorth.textContent = formatRupiah(net.netWorth);
    if (elRepCash) elRepCash.textContent = formatRupiah(net.totalCash);
    if (elRepAssets) elRepAssets.textContent = formatRupiah(net.totalAssets);
    if (elRepInvTotal) elRepInvTotal.textContent = formatRupiah(net.totalInvestments);
    if (elRepDebtsTotal) elRepDebtsTotal.textContent = formatRupiah(net.totalDebts);
  }

  // Workflows & Glossary
  function renderWorkflowsAndGlossary() {
    const wfContainer = document.getElementById('workflow-list');
    if (wfContainer) {
      wfContainer.innerHTML = db.workflows.map((wf, idx) => `
        <div class="p-3.5 bg-slate-800/80 rounded-xl border border-slate-700/60 space-y-1">
          <div class="flex items-center justify-between">
            <span class="text-xs font-bold text-emerald-400">${wf.step}</span>
            <span class="text-[10px] px-2 py-0.5 rounded-full bg-slate-700 text-slate-300 font-semibold">${wf.frequency}</span>
          </div>
          <div class="text-xs font-medium text-slate-200">${wf.action}</div>
          <div class="text-[11px] text-slate-400 bg-slate-900/60 p-2 rounded-lg border border-slate-800">
            <strong>Kontrol:</strong> ${wf.control}
          </div>
        </div>
      `).join('');
    }

    const glossContainer = document.getElementById('glossary-list');
    if (glossContainer) {
      glossContainer.innerHTML = db.glossary.map(g => `
        <div class="p-3 bg-slate-800/80 rounded-xl border border-slate-700/60">
          <div class="text-xs font-bold text-indigo-400">${g.term}</div>
          <div class="text-xs text-slate-300 mt-0.5">${g.def}</div>
        </div>
      `).join('');
    }
  }

  // Tab switching
  function switchTab(tabName) {
    activeTab = tabName;
    document.querySelectorAll('.tab-content').forEach(el => el.classList.add('hidden'));
    const target = document.getElementById(`tab-section-${tabName}`);
    if (target) target.classList.remove('hidden');

    // Update bottom navigation icons
    document.querySelectorAll('.bottom-nav-btn').forEach(btn => {
      const active = btn.dataset.tab === tabName;
      if (active) {
        btn.classList.add('text-emerald-400', 'scale-105');
        btn.classList.remove('text-slate-400');
      } else {
        btn.classList.remove('text-emerald-400', 'scale-105');
        btn.classList.add('text-slate-400');
      }
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Modals & Actions
  function openNewTransactionModal(presetType = 'Expense') {
    const modal = document.getElementById('modal-transaction');
    if (!modal) return;

    // Populate dropdowns
    const typeSelect = document.getElementById('modal-tx-type');
    typeSelect.value = presetType;

    updateCategoryDropdown();

    const accSelect = document.getElementById('modal-tx-account');
    accSelect.innerHTML = db.accounts.map(a => `<option value="${a.id}">${a.name} (${a.type})</option>`).join('');

    // Default date to today
    document.getElementById('modal-tx-date').value = new Date().toISOString().substring(0, 10);
    document.getElementById('modal-tx-desc').value = '';
    document.getElementById('modal-tx-amount').value = '';
    document.getElementById('modal-tx-party').value = '';
    document.getElementById('modal-tx-notes').value = '';

    modal.classList.remove('hidden');
    modal.classList.add('flex');
  }

  function closeTransactionModal() {
    const modal = document.getElementById('modal-transaction');
    if (modal) {
      modal.classList.add('hidden');
      modal.classList.remove('flex');
    }
  }

  function updateCategoryDropdown() {
    const type = document.getElementById('modal-tx-type').value;
    const catSelect = document.getElementById('modal-tx-category');
    const filteredCats = db.categories.filter(c => c.type === type && c.active);
    catSelect.innerHTML = filteredCats.map(c => `<option value="${c.name}">${c.name}</option>`).join('');
  }

  function saveTransactionFromModal(e) {
    e.preventDefault();
    const type = document.getElementById('modal-tx-type').value;
    const category = document.getElementById('modal-tx-category').value;
    const accountId = document.getElementById('modal-tx-account').value;
    const date = document.getElementById('modal-tx-date').value;
    const amount = Number(document.getElementById('modal-tx-amount').value);
    const desc = document.getElementById('modal-tx-desc').value.trim();
    const party = document.getElementById('modal-tx-party').value.trim();
    const notes = document.getElementById('modal-tx-notes').value.trim();

    if (!amount || amount <= 0) {
      alert('Mohon masukkan nominal yang valid!');
      return;
    }
    if (!date) {
      alert('Mohon pilih tanggal transaksi!');
      return;
    }

    const newId = `TX-${String(Date.now()).slice(-5)}`;
    const newTx = {
      id: newId,
      date,
      type,
      desc: desc || category,
      category,
      accountId,
      amount,
      party,
      transferId: '',
      status: 'Posted',
      notes
    };

    db.transactions.unshift(newTx);
    saveDatabase();
    closeTransactionModal();
    renderAll();
    showToast(`Transaksi ${formatRupiah(amount)} berhasil dicatat!`);
  }

  function deleteTransaction(id) {
    if (!confirm('Hapus transaksi ini? Saldo dan laporan akan dihitung ulang.')) return;
    db.transactions = db.transactions.filter(t => t.id !== id);
    saveDatabase();
    renderAll();
    showToast('Transaksi dihapus.');
  }

  function openDepositGoalModal(goalId) {
    const goal = db.goals.find(g => g.id === goalId);
    if (!goal) return;
    const amountStr = prompt(`Masukkan nominal setoran tambahan untuk target "${goal.name}":`, '500000');
    if (!amountStr) return;
    const addAmt = Number(amountStr);
    if (isNaN(addAmt) || addAmt <= 0) {
      alert('Nominal tidak valid.');
      return;
    }

    goal.currentAmount = (Number(goal.currentAmount) || 0) + addAmt;
    saveDatabase();
    renderAll();
    showToast(`Dana target bertambah ${formatRupiah(addAmt)}!`);
  }

  // Export / Import
  function exportDataJSON() {
    const dataStr = 'data:text/json;charset=utf-8,' + encodeURIComponent(JSON.stringify(db, null, 2));
    const downloadAnchor = document.createElement('a');
    downloadAnchor.setAttribute('href', dataStr);
    downloadAnchor.setAttribute('download', `NusaPersonal_Backup_${new Date().toISOString().substring(0, 10)}.json`);
    document.body.appendChild(downloadAnchor);
    downloadAnchor.click();
    downloadAnchor.remove();
    showToast('Cadangan data berhasil diunduh!');
  }

  function exportTransactionsCSV() {
    const headers = ['ID Transaksi', 'Tanggal', 'Jenis', 'Deskripsi', 'Kategori', 'ID Akun', 'Nominal', 'Pihak', 'Status', 'Catatan'];
    const rows = db.transactions.map(t => [
      `"${t.id}"`,
      `"${t.date}"`,
      `"${t.type}"`,
      `"${(t.desc || '').replace(/"/g, '""')}"`,
      `"${t.category}"`,
      `"${t.accountId}"`,
      t.amount,
      `"${(t.party || '').replace(/"/g, '""')}"`,
      `"${t.status}"`,
      `"${(t.notes || '').replace(/"/g, '""')}"`
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `DB_TRANSAKSI_${activePeriod}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    showToast('CSV Transaksi berhasil diekspor!');
  }

  function importDataJSON(file) {
    const reader = new FileReader();
    reader.onload = function (e) {
      try {
        const imported = JSON.parse(e.target.result);
        if (imported.transactions && imported.accounts) {
          db = imported;
          saveDatabase();
          renderAll();
          showToast('Data berhasil diimpor!');
        } else {
          alert('Format file JSON tidak valid!');
        }
      } catch (err) {
        alert('Gagal membaca file JSON: ' + err.message);
      }
    };
    reader.readAsText(file);
  }

  // Expose to window for UI onclicks
  window.nusaApp = {
    switchTab,
    openNewTransactionModal,
    closeTransactionModal,
    updateCategoryDropdown,
    saveTransactionFromModal,
    deleteTransaction,
    openDepositGoalModal,
    resetToDefaultData,
    exportDataJSON,
    exportTransactionsCSV,
    importDataJSON
  };

  // DOM Loaded init
  document.addEventListener('DOMContentLoaded', () => {
    loadDatabase();

    // Period selector change
    const pSel = document.getElementById('period-selector');
    if (pSel) {
      pSel.addEventListener('change', (e) => {
        activePeriod = e.target.value;
        renderAll();
      });
    }

    // Modal type change listener
    const modalType = document.getElementById('modal-tx-type');
    if (modalType) {
      modalType.addEventListener('change', updateCategoryDropdown);
    }

    // Modal submit
    const txForm = document.getElementById('form-new-tx');
    if (txForm) {
      txForm.addEventListener('submit', saveTransactionFromModal);
    }

    // Tx filters
    const filterType = document.getElementById('tx-filter-type');
    if (filterType) filterType.addEventListener('change', renderTransactions);
    const searchInput = document.getElementById('tx-search');
    if (searchInput) searchInput.addEventListener('input', renderTransactions);

    // Initial render
    renderAll();

    // Register Service Worker for Android PWA
    if ('serviceWorker' in navigator) {
      window.addEventListener('load', () => {
        navigator.serviceWorker.register('./sw.js').then((reg) => {
          console.log('[PWA] Service Worker registered:', reg.scope);
        }).catch((err) => {
          console.log('[PWA] Service Worker registration failed:', err);
        });
      });
    }
  });

})();
