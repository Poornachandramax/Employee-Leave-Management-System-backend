const $ = (id) => document.getElementById(id);
let currentUser = null;
let employeeLeaves = [];
let adminLeaves = [];

function toast(message) {
  const t = $('toast'); t.textContent = message; t.classList.remove('hidden');
  clearTimeout(window.toastTimer); window.toastTimer = setTimeout(() => t.classList.add('hidden'), 3200);
}
async function api(url, options = {}) {
  const response = await fetch(url, { headers: {'Content-Type':'application/json', ...(options.headers||{})}, ...options });
  const text = await response.text();
  let data = null; try { data = text ? JSON.parse(text) : null; } catch {}
  if (!response.ok) throw new Error(data?.message || `Request failed (${response.status})`);
  return data;
}
$('loginForm').addEventListener('submit', async (event) => {
  event.preventDefault(); $('loginError').textContent = '';
  try {
    currentUser = await api('/api/auth/login', {method:'POST', body:JSON.stringify({username:$('username').value.trim(), password:$('password').value})});
    $('loginView').classList.add('hidden'); $('appView').classList.remove('hidden');
    $('sideName').textContent = currentUser.fullName; $('topName').textContent = currentUser.fullName;
    $('sideRole').textContent = currentUser.role === 'ADMIN' ? 'Administrator' : 'Employee';
    $('avatar').textContent = currentUser.fullName.charAt(0).toUpperCase();
    $('employeePanel').classList.toggle('hidden', currentUser.role === 'ADMIN');
    $('adminPanel').classList.toggle('hidden', currentUser.role !== 'ADMIN');
    $('navRequestsText').textContent = currentUser.role === 'ADMIN' ? 'All requests' : 'My leave requests';
    $('welcomeName').textContent = `Hello, ${currentUser.fullName.split(' ')[0]}!`;
    await refresh();
  } catch (e) { $('loginError').textContent = e.message; }
});
async function refresh() {
  if (currentUser.role === 'ADMIN') {
    adminLeaves = await api('/api/admin/leaves'); renderAdmin();
  } else {
    const [leaves, balance] = await Promise.all([
      api(`/api/leaves?username=${encodeURIComponent(currentUser.username)}`),
      api(`/api/leave-balance?username=${encodeURIComponent(currentUser.username)}`)
    ]);
    employeeLeaves = leaves;
    $('annualBal').textContent = balance.annual; $('sickBal').textContent = balance.sick; $('casualBal').textContent = balance.casual;
    $('annualProgress').style.width = `${Math.min(100,balance.annual/12*100)}%`;
    $('sickProgress').style.width = `${Math.min(100,balance.sick/6*100)}%`;
    $('casualProgress').style.width = `${Math.min(100,balance.casual/6*100)}%`;
    renderEmployee();
  }
}
function badge(status){return `<span class="status ${status}">${status[0]+status.slice(1).toLowerCase()}</span>`}
function prettyType(type){return ({ANNUAL:'Annual leave',SICK:'Sick leave',CASUAL:'Casual leave'})[type]||type}
function dateRange(a,b){return `${a} – ${b}`}
function renderEmployee(){
  const filter=$('employeeFilter').value;
  const rows=employeeLeaves.filter(l=>filter==='ALL'||l.status===filter);
  $('employeeRows').innerHTML=rows.length?rows.map(l=>`<tr><td><span class="type-name">${prettyType(l.leaveType)}</span></td><td>${dateRange(l.startDate,l.endDate)}</td><td>${l.days}</td><td>${badge(l.status)}</td><td title="${escapeHtml(l.reason)}">${escapeHtml(l.reason.length>32?l.reason.slice(0,32)+'…':l.reason)}</td></tr>`).join(''):`<tr><td colspan="5" class="empty">No leave requests yet. Apply for leave to get started.</td></tr>`;
}
function renderAdmin(){
  const filter=$('adminFilter').value;
  $('totalCount').textContent=adminLeaves.length;
  $('pendingCount').textContent=adminLeaves.filter(l=>l.status==='PENDING').length;
  $('approvedCount').textContent=adminLeaves.filter(l=>l.status==='APPROVED').length;
  const rows=adminLeaves.filter(l=>filter==='ALL'||l.status===filter);
  $('adminRows').innerHTML=rows.length?rows.map(l=>`<tr><td><div class="person"><span class="avatar">${escapeHtml(l.employeeName.charAt(0))}</span><div><strong>${escapeHtml(l.employeeName)}</strong><small>${escapeHtml(l.username)}</small></div></div></td><td><span class="type-name">${prettyType(l.leaveType)}</span></td><td>${dateRange(l.startDate,l.endDate)}</td><td>${l.days}</td><td>${badge(l.status)}</td><td title="${escapeHtml(l.reason)}">${escapeHtml(l.reason.length>24?l.reason.slice(0,24)+'…':l.reason)}</td><td>${l.status==='PENDING'?`<button class="table-action approve" onclick="decide(${l.id},'APPROVED')">Approve</button><button class="table-action reject" onclick="decide(${l.id},'REJECTED')">Reject</button>`:'—'}</td></tr>`).join(''):`<tr><td colspan="7" class="empty">No requests match this filter.</td></tr>`;
}
function escapeHtml(value){return String(value??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
window.decide = async (id,status) => {
  const comment = status==='REJECTED' ? (prompt('Optional reason for rejection:')||'') : '';
  if(!confirm(`${status==='APPROVED'?'Approve':'Reject'} this leave request?`)) return;
  try { await api(`/api/admin/leaves/${id}`,{method:'PATCH',body:JSON.stringify({status,comment})}); toast(`Request ${status.toLowerCase()}.`); await refresh(); }
  catch(e){toast(e.message)}
};
$('showApply').addEventListener('click',()=>{$('applyCard').classList.remove('hidden');$('applyCard').scrollIntoView({behavior:'smooth',block:'start'})});
$('closeApply').addEventListener('click',()=>$('applyCard').classList.add('hidden'));
$('leaveForm').addEventListener('submit',async e=>{
  e.preventDefault();$('leaveError').textContent='';
  const payload={username:currentUser.username,leaveType:$('leaveType').value,startDate:$('startDate').value,endDate:$('endDate').value,reason:$('reason').value.trim()};
  try{await api('/api/leaves',{method:'POST',body:JSON.stringify(payload)});$('leaveForm').reset();$('applyCard').classList.add('hidden');toast('Leave request submitted successfully.');await refresh()}
  catch(err){$('leaveError').textContent=err.message}
});
$('employeeFilter').addEventListener('change',renderEmployee);
$('adminFilter').addEventListener('change',renderAdmin);
$('navDashboard').addEventListener('click',()=>{ $('pageTitle').textContent='Dashboard'; $('navDashboard').classList.add('active');$('navRequests').classList.remove('active'); window.scrollTo({top:0,behavior:'smooth'})});
$('navRequests').addEventListener('click',()=>{ $('pageTitle').textContent=currentUser?.role==='ADMIN'?'All leave requests':'My leave requests';$('navRequests').classList.add('active');$('navDashboard').classList.remove('active');document.querySelector('.panel')?.scrollIntoView({behavior:'smooth'})});
$('logoutBtn').addEventListener('click',()=>{currentUser=null;$('appView').classList.add('hidden');$('loginView').classList.remove('hidden');$('password').value='';$('loginError').textContent='';});
const today=new Date(); const localDate=new Date(today.getTime()-today.getTimezoneOffset()*60000).toISOString().slice(0,10);
$('startDate').min=localDate;$('endDate').min=localDate;
$('startDate').addEventListener('change',()=>{$('endDate').min=$('startDate').value||localDate;if($('endDate').value<$('startDate').value)$('endDate').value=$('startDate').value});
