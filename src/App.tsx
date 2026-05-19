/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState } from 'react';
import { 
  Home, 
  Calendar, 
  History, 
  User, 
  Stethoscope, 
  Settings, 
  CreditCard,
  LogOut,
  ChevronRight,
  Bell,
  Search,
  Plus,
  Clock,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

// Types
type Role = 'PARENT' | 'DOCTOR' | 'ADMIN';

export default function App() {
  const [role, setRole] = useState<Role | null>(null);
  const [activeTab, setActiveTab] = useState('home');

  // Unified Shell to prevent "jumping" UI
  const SmartphoneShell = ({ children }: { children: React.ReactNode }) => (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-pattern pointer-events-none" />
      <div className="mobile-container relative z-10 transition-all duration-500">
        <div className="iphone-notch" />
        {children}
      </div>
    </div>
  );

  if (!role) {
    return (
      <SmartphoneShell>
        <LoginView onLogin={(selectedRole) => setRole(selectedRole)} />
      </SmartphoneShell>
    );
  }

  return (
    <SmartphoneShell>
      {/* Role Switcher overlay for preview ease */}
      <div className="fixed top-6 right-6 z-50 flex gap-2 bg-white p-2 rounded-full shadow-2xl border border-slate-100 group">
        <button onClick={() => setRole(null)} className="p-2 hover:bg-slate-100 rounded-full text-slate-400 transition-colors"><LogOut size={16} /></button>
        <div className="h-4 w-[1px] bg-slate-200 my-auto" />
        {(['PARENT', 'DOCTOR', 'ADMIN'] as Role[]).map((r) => (
          <button
            key={r}
            onClick={() => { setRole(r); setActiveTab('home'); }}
            className={`px-4 py-1.5 rounded-full text-[10px] font-bold transition-all ${
              role === r ? 'bg-primary text-white shadow-lg' : 'text-slate-400 hover:text-slate-600'
            }`}
          >
            {r}
          </button>
        ))}
      </div>

      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header - Fixed Height */}
        <header className="px-6 pt-14 pb-8 bg-primary rounded-b-[45px] shadow-lg relative overflow-hidden flex-shrink-0">
          <div className="absolute top-[-30px] right-[-30px] w-48 h-48 bg-white/10 rounded-full" />
          <div className="relative z-10 flex justify-between items-center text-white">
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 bg-white/20 backdrop-blur-md rounded-2xl flex items-center justify-center border border-white/30">
                <Stethoscope size={22} className="text-white" />
              </div>
              <div>
                <p className="text-sky-100 text-[10px] font-bold uppercase tracking-widest opacity-80">CeriaCare App</p>
                <h1 className="font-bold text-lg leading-tight tracking-tight">
                  {role === 'PARENT' ? 'Bunda Sarah 👋' : role === 'DOCTOR' ? 'dr. Budi, Sp.A' : 'System Admin'}
                </h1>
              </div>
            </div>
            <button className="w-11 h-11 bg-white/20 backdrop-blur-md rounded-full flex items-center justify-center border border-white/30">
              <Bell size={20} />
            </button>
          </div>
          
          {activeTab === 'home' && role === 'PARENT' && (
            <motion.div 
              initial={{ y: 20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              className="mt-6 glass-card p-4 flex items-center gap-4"
            >
              <div className="w-12 h-12 bg-sun rounded-2xl flex items-center justify-center text-2xl shadow-inner">🧸</div>
              <div>
                <p className="text-[10px] text-sky-50 font-black uppercase tracking-widest opacity-90">Jadwal Terdekat</p>
                <p className="text-sm font-bold">Resep Siap: Arka (5th)</p>
                <p className="text-[11px] opacity-70">Apotek Klinik • Lantai 1</p>
              </div>
            </motion.div>
          )}
        </header>

        {/* Dynamic Main Body */}
        <main className="flex-1 overflow-y-auto bg-[#F8FAFC] pb-24 scrollbar-hide">
          <AnimatePresence mode="wait">
            {role === 'PARENT' && <ParentView activeTab={activeTab} setActiveTab={setActiveTab} key="parent" />}
            {role === 'DOCTOR' && <DoctorView activeTab={activeTab} setActiveTab={setActiveTab} key="doctor" />}
            {role === 'ADMIN' && <AdminView activeTab={activeTab} setActiveTab={setActiveTab} key="admin" />}
          </AnimatePresence>
        </main>

        {/* Unified Bottom Nav */}
        <nav className="absolute bottom-0 left-0 right-0 bg-white border-t border-slate-50 px-8 py-4 flex justify-between items-center rounded-t-[45px] shadow-[0_-15px_40px_rgba(0,0,0,0.04)] z-30 h-24">
          <NavItems role={role} activeTab={activeTab} setActiveTab={setActiveTab} />
        </nav>
      </div>
    </SmartphoneShell>
  );
}

function NavButton({ active, icon: Icon, label, onClick }: { active: boolean, icon: any, label: string, onClick: () => void }) {
  return (
    <button 
      onClick={onClick}
      className={`flex flex-col items-center gap-1 transition-all ${active ? 'text-primary' : 'text-slate-400'}`}
    >
      <div className={`p-2 rounded-2xl transition-all ${active ? 'bg-primary/10' : ''}`}>
        <Icon size={24} strokeWidth={active ? 2.5 : 2} />
      </div>
      <span className="text-[10px] font-bold uppercase tracking-wide">{label}</span>
      {active && <motion.div layoutId="nav-dot" className="w-1 h-1 rounded-full bg-primary" />}
    </button>
  );
}

function NavItems({ role, activeTab, setActiveTab }: { role: Role, activeTab: string, setActiveTab: (t: string) => void }) {
  if (role === 'PARENT') return (
    <>
      <NavButton active={activeTab === 'home'} icon={Home} label="Home" onClick={() => setActiveTab('home')} />
      <NavButton active={activeTab === 'booking'} icon={Calendar} label="Booking" onClick={() => setActiveTab('booking')} />
      <NavButton active={activeTab === 'payment'} icon={CreditCard} label="Bayar" onClick={() => setActiveTab('payment')} />
      <NavButton active={activeTab === 'profile'} icon={User} label="Profil" onClick={() => setActiveTab('profile')} />
    </>
  );
  if (role === 'DOCTOR') return (
    <>
      <NavButton active={activeTab === 'home'} icon={Home} label="Antrian" onClick={() => setActiveTab('home')} />
      <NavButton active={activeTab === 'history'} icon={History} label="Rekam Medis" onClick={() => setActiveTab('history')} />
      <NavButton active={activeTab === 'profile'} icon={User} label="Profil" onClick={() => setActiveTab('profile')} />
    </>
  );
  return (
    <>
      <NavButton active={activeTab === 'home'} icon={Home} label="Monitor" onClick={() => setActiveTab('home')} />
      <NavButton active={activeTab === 'payment'} icon={CreditCard} label="Keuangan" onClick={() => setActiveTab('payment')} />
      <NavButton active={activeTab === 'settings'} icon={Settings} label="Master" onClick={() => setActiveTab('settings')} />
    </>
  );
}

function LoginView({ onLogin, onGoToRegister }: { onLogin: (role: Role) => void, onGoToRegister: () => void }) {
  return (
    <div className="p-8 flex flex-col items-center justify-center text-center h-full bg-white space-y-10 relative overflow-hidden">
      <div className="absolute top-[-10%] right-[-10%] w-64 h-64 bg-primary/5 rounded-full blur-3xl" />
      
      <motion.div 
        initial={{ scale: 0.5, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        className="w-32 h-32 bg-primary rounded-[45px] flex items-center justify-center text-white shadow-2xl shadow-primary/20 relative z-10"
      >
        <div className="absolute inset-2 border-2 border-white/20 rounded-[35px]" />
        <Stethoscope size={64} strokeWidth={2.5} />
      </motion.div>

      <div className="space-y-3 z-10">
        <h1 className="text-5xl font-black text-brand-blue tracking-tighter">CeriaCare</h1>
        <p className="text-slate-400 font-bold uppercase tracking-[0.3em] text-[10px]">Pediatric Care Redefined</p>
      </div>

      <div className="w-full space-y-5 pt-8 z-10">
        <button onPointerDownCapture={(e) => e.stopPropagation()} onClick={() => onLogin('PARENT')} className="w-full btn-primary h-16 text-base tracking-widest shadow-xl flex items-center justify-center gap-3">
          Masuk Sebagai Bunda <ChevronRight size={20} />
        </button>
        <div className="flex gap-4">
          <button onClick={() => onLogin('DOCTOR')} className="flex-1 py-5 rounded-[30px] bg-slate-50 text-slate-600 font-bold border border-slate-100 hover:bg-primary/5 hover:border-primary/20 transition-all flex flex-col items-center gap-2">
            <User size={24} className="text-secondary" />
            <span className="text-[10px] uppercase tracking-wider">Dokter</span>
          </button>
          <button onClick={() => onLogin('ADMIN')} className="flex-1 py-5 rounded-[30px] bg-slate-50 text-slate-600 font-bold border border-slate-100 hover:bg-accent/5 hover:border-accent/20 transition-all flex flex-col items-center gap-2">
            <Settings size={24} className="text-accent" />
            <span className="text-[10px] uppercase tracking-wider">Admin</span>
          </button>
        </div>
      </div>
      
      <div className="pt-12 text-[9px] font-black uppercase text-slate-300 tracking-[0.4em]">Official Clinic Management System</div>
      
      <div className="pt-4 z-10">
        <button onClick={onGoToRegister} className="text-primary text-[10px] font-bold uppercase tracking-widest hover:underline transition-all">Belum punya akun? Registrasi Baru</button>
      </div>
    </div>
  );
}

function RegisterView({ onRegister, onBack }: { onRegister: () => void, onBack: () => void }) {
  return (
    <div className="p-8 flex flex-col items-center justify-center bg-white h-full space-y-8 relative overflow-hidden">
      <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary via-secondary to-accent" />
      
      <div className="w-full text-left space-y-2">
        <button onClick={onBack} className="p-2 bg-slate-100 rounded-full mb-4"><ChevronRight className="rotate-180" size={16} /></button>
        <h2 className="text-3xl font-black text-brand-blue tracking-tight">Daftar Akun</h2>
        <p className="text-slate-400 text-sm font-medium">Bergabung dengan komunitas CeriaCare sekarang.</p>
      </div>

      <div className="w-full space-y-4">
        {[
          { label: 'Nama Lengkap', type: 'text', icon: User },
          { label: 'Email', type: 'email', icon: Search },
          { label: 'Password', type: 'password', icon: Settings },
          { label: 'Konfirmasi Password', type: 'password', icon: Settings },
        ].map((field, i) => (
          <div key={i} className="space-y-1">
            <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">{field.label}</label>
            <div className="relative group">
              <input type={field.type} className="w-full p-4 pl-12 rounded-2xl bg-slate-50 border border-slate-100 font-medium outline-none transition-all focus:ring-2 focus:ring-primary/20 focus:bg-white" />
              <field.icon className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-primary transition-colors" size={20} />
            </div>
          </div>
        ))}
      </div>

      <div className="w-full space-y-4 pt-4">
        <button onClick={onRegister} className="w-full btn-primary h-14 text-sm tracking-widest flex items-center justify-center gap-2">
          Daftar Sekarang <Plus size={18} />
        </button>
        <p className="text-center text-[10px] text-slate-400 font-medium leading-relaxed">
          Dengan mendaftar, Anda menyetujui <span className="text-primary font-bold">Syarat & Ketentuan</span> yang berlaku.
        </p>
      </div>
    </div>
  );
}

function ParentView({ activeTab, setActiveTab }: { activeTab: string, setActiveTab: (t: string) => void }) {
  const [selectedPayment, setSelectedPayment] = useState<any>(null);

  if (activeTab === 'payment' || selectedPayment) {
    return (
      <motion.div initial={{ x: 20, opacity: 0 }} animate={{ x: 0, opacity: 1 }} className="p-6 space-y-6">
        <h2 className="text-2xl font-bold text-brand-blue">Pembayaran 💳</h2>
        <div className="space-y-4">
          {[{ id: 'INV-009', item: 'Pemeriksaan Umum - Arka', price: 'Rp 250.000', status: 'PENDING' }, { id: 'INV-008', item: 'Vaksin DPT - Ziva', price: 'Rp 450.000', status: 'PAID' }].map((inv, i) => (
            <div key={i} className="card-bubble border-slate-100">
              <div className="flex justify-between items-start mb-2">
                <span className="text-[10px] font-mono text-slate-400">{inv.id}</span>
                <span className={`text-[9px] font-black px-2 py-1 rounded-full ${inv.status === 'PAID' ? 'bg-accent/10 text-accent' : 'bg-secondary/10 text-secondary'}`}>
                  {inv.status}
                </span>
              </div>
              <h4 className="font-bold text-slate-700">{inv.item}</h4>
              <p className="text-lg font-black text-brand-blue mt-1">{inv.price}</p>
              {inv.status === 'PENDING' && (
                <button className="w-full mt-4 py-3 bg-secondary text-white rounded-2xl font-bold text-xs uppercase tracking-widest shadow-lg shadow-secondary/20">
                  Bayar Sekarang
                </button>
              )}
            </div>
          ))}
        </div>
      </motion.div>
    );
  }

  if (activeTab === 'booking') {
    return (
      <motion.div initial={{ x: 20, opacity: 0 }} animate={{ x: 0, opacity: 1 }} className="p-6 space-y-6">
        <h2 className="text-2xl font-bold text-brand-blue">Booking Jadwal</h2>
        <div className="card-bubble space-y-6">
          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Pilih Anak</label>
            <div className="flex gap-2">
              <button className="px-5 py-2.5 rounded-full bg-primary text-white font-bold text-xs shadow-lg shadow-primary/20">Arka</button>
              <button className="px-5 py-2.5 rounded-full bg-slate-50 text-slate-400 font-bold text-xs border border-slate-100">Ziva</button>
              <button className="px-5 py-2.5 rounded-full border-2 border-dashed border-slate-200 text-slate-300 flex items-center justify-center"><Plus size={16}/></button>
            </div>
          </div>
          <div className="space-y-4">
              <div className="space-y-1">
                <label className="text-[9px] font-black text-slate-300 uppercase">Layanan</label>
                <div className="p-4 bg-slate-50 rounded-2xl font-bold text-slate-600 border border-slate-100">Konsultasi Umum</div>
              </div>
              <div className="space-y-1">
                <label className="text-[9px] font-black text-slate-300 uppercase">Dokter</label>
                <div className="p-4 bg-slate-50 rounded-2xl font-bold text-slate-600 border border-slate-100">dr. Sarah Wijaya, Sp.A</div>
              </div>
          </div>
          <button className="w-full btn-primary">Konfirmasi Jadwal</button>
        </div>
      </motion.div>
    );
  }

  if (activeTab === 'home') {
    return (
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-8">
        {/* Services Grid per Use Case */}
        <section>
          <h3 className="text-brand-blue font-black text-xs uppercase tracking-[0.2em] mb-5">Layanan Utama</h3>
          <div className="grid grid-cols-4 gap-4">
            {[
              { icon: '📅', label: 'Booking', t: 'booking', bg: 'bg-primary/10 border-primary/20' },
              { icon: '👨‍⚕️', label: 'Dokter', t: 'home', bg: 'bg-secondary/10 border-secondary/20' },
              { icon: '🏥', label: 'Riwayat', t: 'history', bg: 'bg-accent/10 border-accent/20' },
              { icon: '💳', label: 'Tagihan', t: 'payment', bg: 'bg-sun/10 border-sun/20' }
            ].map((item, i) => (
              <button key={i} onClick={() => setActiveTab(item.t)} className="flex flex-col items-center gap-2 group">
                <div className={`w-14 h-14 ${item.bg} border rounded-2xl flex items-center justify-center text-2xl shadow-sm group-hover:scale-105 transition-all`}>
                  {item.icon}
                </div>
                <span className="text-[9px] font-black text-slate-500 uppercase tracking-wider">{item.label}</span>
              </button>
            ))}
          </div>
        </section>

        {/* Online Consult Banner */}
        <div className="bg-[#FDE68A]/30 border border-sun/30 rounded-[35px] p-6 relative overflow-hidden group">
          <div className="absolute top-[-20%] right-[-10%] w-32 h-32 bg-white/40 rounded-full blur-2xl group-hover:scale-110 transition-transform" />
          <div className="relative z-10 pr-16">
            <h4 className="text-[#92400E] font-black text-lg">Chat Dokter</h4>
            <p className="text-[11px] text-[#92400E]/70 mt-1 font-medium leading-relaxed">Konsultasi pertolongan pertama 24/7 bersama admin medis.</p>
            <button className="mt-4 bg-secondary text-white px-5 py-2 rounded-full text-[10px] font-black uppercase tracking-widest shadow-lg shadow-secondary/30">Mulai</button>
          </div>
          <div className="absolute right-4 bottom-4 w-16 h-16 bg-white rounded-full flex items-center justify-center shadow-inner text-3xl">💬</div>
        </div>

        {/* Queue Progress Monitor */}
        <section className="space-y-4">
          <div className="flex justify-between items-center px-1">
            <h3 className="font-black text-sm text-brand-blue uppercase tracking-widest">Pantau Antrian</h3>
            <span className="bg-accent/10 text-accent text-[9px] font-black px-2 py-0.5 rounded-full">LIVE</span>
          </div>
          <div className="card-bubble border-primary/10">
            <div className="flex items-center gap-6">
              <div className="w-20 h-20 bg-primary/5 rounded-[30px] flex flex-col items-center justify-center border border-primary/10">
                <span className="text-[9px] font-black text-primary/50 uppercase tracking-widest">Antrian</span>
                <span className="text-3xl font-black text-primary">A-12</span>
              </div>
              <div className="flex-1 space-y-2">
                <div>
                  <h5 className="font-black text-slate-700 text-sm">Arka Pratama</h5>
                  <p className="text-[11px] text-slate-400 font-bold uppercase tracking-wider">Estimasi 10:45 • dr. Sarah</p>
                </div>
                <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                  <motion.div initial={{ width: 0 }} animate={{ width: '70%' }} className="h-full bg-accent" />
                </div>
                <p className="text-[10px] font-bold text-accent text-right">Lagi Diperiksa (2 Antrian Lagi)</p>
              </div>
            </div>
          </div>
        </section>
      </motion.div>
    );
  }

  if (activeTab === 'history') {
    return (
      <div className="p-6 space-y-6">
        <h2 className="text-2xl font-bold">Riwayat Medis</h2>
        <div className="space-y-4">
          {[1,2,3].map(i => (
            <div key={i} className="card-bubble space-y-4 border-slate-100">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-bold text-lg">Poli Tumbuh Kembang</h3>
                  <p className="text-xs font-bold text-slate-400 uppercase">10 April 2024 • dr. Sarah Wijaya</p>
                </div>
                <div className="bg-secondary/10 text-secondary px-2 py-1 rounded-lg text-[10px] font-bold">RESEP SIAP</div>
              </div>
              <div className="bg-slate-50 p-4 rounded-2xl flex items-center gap-4">
                <History size={20} className="text-slate-400" />
                <div className="text-sm font-medium text-slate-600">Hasil: Berat badan normal, tinggi bertambah 2cm. Disarankan pemberian vitamin D.</div>
              </div>
              <div className="flex gap-2">
                <button className="flex-1 py-2 text-xs font-bold text-primary bg-primary/10 rounded-xl">Lihat Resep</button>
                <button className="flex-1 py-2 text-xs font-bold text-slate-500 bg-slate-100 rounded-xl">Invoice</button>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return <div className="p-10 text-center font-bold text-slate-300">Coming Soon!</div>;
}

function DoctorView({ activeTab, setActiveTab }: { activeTab: string, setActiveTab: (t: string) => void }) {
  const [selectedPatient, setSelectedPatient] = useState<any>(null);

  if (selectedPatient) {
    return (
      <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} className="p-6 space-y-6 pb-32">
        <div className="flex items-center gap-4">
          <button onClick={() => setSelectedPatient(null)} className="p-2 bg-white rounded-xl shadow-sm"><ChevronRight className="rotate-180" /></button>
          <div>
            <h2 className="text-2xl font-bold">{selectedPatient.name}</h2>
            <p className="text-xs font-bold text-slate-400 uppercase">Antrian {selectedPatient.no} • {selectedPatient.age}</p>
          </div>
        </div>

        <div className="space-y-6">
          <div className="card-bubble border-slate-100 space-y-4">
             <h3 className="font-bold text-slate-700 flex items-center gap-2"><History size={18} /> Riwayat Terakhir</h3>
             <p className="text-sm text-slate-500 italic">"Terakhir kontrol 2 minggu lalu. Demam sudah turun, nafas normal."</p>
          </div>

          <div className="space-y-4">
            <h3 className="font-bold text-lg px-1">Pemeriksaan Hari Ini</h3>
            <div className="card-bubble border-slate-100 space-y-6">
               <div className="space-y-2">
                 <label className="text-xs font-bold text-slate-400 uppercase">Keluhan / Diagnosa</label>
                 <textarea className="w-full p-4 rounded-2xl bg-slate-50 border-none text-sm min-h-[100px]" placeholder="Masukkan hasil observasi..." />
               </div>
               <div className="space-y-2">
                 <label className="text-xs font-bold text-slate-400 uppercase">Resep Obat</label>
                 <div className="flex gap-2">
                    <input className="flex-1 p-3 rounded-xl bg-slate-50 border-none text-xs" placeholder="Nama Obat" />
                    <button className="p-3 bg-secondary text-white rounded-xl"><Plus size={16} /></button>
                 </div>
               </div>
               <button onClick={() => setSelectedPatient(null)} className="btn-primary w-full">Simpan & Selesai</button>
            </div>
          </div>
        </div>
      </motion.div>
    );
  }

  if (activeTab === 'history') {
    return (
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-6">
        <h2 className="text-2xl font-bold text-brand-blue">Selesai Dirawat ✅</h2>
        <div className="space-y-4">
          {[
            { name: 'Kania Rara', age: '4 thn', date: 'Hari Ini, 09:12', spec: 'Poli Umum' },
            { name: 'Farhan Adi', age: '7 thn', date: 'Hari Ini, 08:45', spec: 'Poli Umum' },
            { name: 'Ziva Putri', age: '2 thn', date: 'Kemarin, 14:20', spec: 'Vaksinasi' }
          ].map((p, i) => (
            <div key={i} className="card-bubble border-slate-100 flex items-center gap-4 bg-white/50">
              <div className="w-12 h-12 bg-accent/10 rounded-xl flex items-center justify-center text-accent"><CheckCircle2 size={24} /></div>
              <div className="flex-1">
                <h4 className="font-bold text-slate-800">{p.name}</h4>
                <p className="text-[11px] text-slate-400 font-medium uppercase tracking-tight">{p.spec} • {p.date}</p>
              </div>
              <button className="p-2 text-primary"><Search size={18} /></button>
            </div>
          ))}
        </div>
      </motion.div>
    );
  }

  if (activeTab === 'profile') {
    return (
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-8">
        <div className="flex flex-col items-center gap-4 pt-4">
           <div className="w-24 h-24 rounded-full border-4 border-white shadow-xl overflow-hidden bg-primary/20 flex items-center justify-center">
              <User size={48} className="text-primary" />
           </div>
           <div className="text-center">
              <h2 className="text-2xl font-black text-brand-blue">dr. Budi Santoso, Sp.A</h2>
              <p className="text-slate-400 text-xs font-bold uppercase tracking-widest">ID: #DOC-102931</p>
           </div>
        </div>

        <div className="space-y-4">
          <h3 className="font-black text-[10px] text-slate-300 uppercase tracking-[.3em] ml-2">Pengaturan Dokter</h3>
          <div className="space-y-2">
            {[
              { label: 'Jadwal Praktek', icon: Clock },
              { label: 'Informasi Spesialis', icon: Stethoscope },
              { label: 'Notifikasi Pasien', icon: Bell },
              { label: 'Keamanan Akun', icon: Settings }
            ].map((item, i) => (
              <button key={i} className="card-bubble w-full flex items-center justify-between border-slate-50 hover:bg-slate-50">
                 <div className="flex items-center gap-4">
                    <item.icon size={20} className="text-primary" />
                    <span className="font-bold text-slate-600">{item.label}</span>
                 </div>
                 <ChevronRight size={18} className="text-slate-300" />
              </button>
            ))}
          </div>
        </div>
      </motion.div>
    );
  }

  return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-8">
      <div className="flex justify-between items-end">
        <h2 className="text-2xl font-bold text-brand-blue tracking-tight">Antrian Hari Ini</h2>
        <div className="bg-secondary/10 px-4 py-2 rounded-2xl text-secondary font-bold text-xs">12 Pasien</div>
      </div>

      <div className="space-y-4">
        {[
          { no: 'A-13', name: 'Siska Amelia', age: '3 thn', time: '09:45' },
          { no: 'A-14', name: 'Bobi Junior', age: '1 thn', time: '10:00' },
          { no: 'A-15', name: 'Arka Pratama', age: '5 thn', time: '10:15' },
        ].map((item, i) => (
          <div key={i} onClick={() => setSelectedPatient(item)} className="card-bubble flex items-center gap-4 bg-white border-slate-100 hover:border-secondary/40 cursor-pointer group">
            <div className="w-14 h-14 bg-slate-50 rounded-2xl flex flex-col items-center justify-center group-hover:bg-secondary/10 transition-colors">
              <span className="text-[10px] font-bold text-slate-400">NO</span>
              <span className="text-lg font-bold text-slate-800">{item.no}</span>
            </div>
            <div className="flex-1">
              <h4 className="font-bold text-slate-800">{item.name}</h4>
              <p className="text-xs text-slate-400 font-medium tracking-tight">Menunggu • Estimasi Jam {item.time}</p>
            </div>
            <button className="btn-secondary py-2 px-4 rounded-xl text-[10px] shadow-sm">LAYANI</button>
          </div>
        ))}
      </div>
    </motion.div>
  );
}

function AdminView({ activeTab, setActiveTab }: { activeTab: string, setActiveTab: (t: string) => void }) {
  if (activeTab === 'home') {
    return (
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-8 pb-32">
        <div className="space-y-2">
           <h2 className="text-2xl font-black text-brand-blue tracking-tight">Monitor Aktivitas 🧐</h2>
           <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">Status Sistem Real-time</p>
        </div>

        {/* Live Counters */}
        <div className="grid grid-cols-2 gap-4">
           <div className="card-bubble bg-gradient-to-br from-primary/5 to-white flex flex-col gap-1 p-4 shadow-xl shadow-primary/5">
              <span className="text-[10px] font-black text-primary uppercase">Registrasi Baru</span>
              <span className="text-3xl font-black text-brand-blue">14</span>
              <span className="text-[9px] text-accent font-bold">↑ 20% vs Kemarin</span>
           </div>
           <div className="card-bubble bg-gradient-to-br from-secondary/5 to-white flex flex-col gap-1 p-4 shadow-xl shadow-secondary/5">
              <span className="text-[10px] font-black text-secondary uppercase">Online Now</span>
              <span className="text-3xl font-black text-brand-blue">42</span>
              <span className="text-[9px] text-slate-400 font-bold">User Aktif</span>
           </div>
        </div>

        {/* User Logs */}
        <section className="space-y-4">
           <h3 className="font-bold text-sm text-slate-700 uppercase tracking-wider">Log User Terbaru</h3>
           <div className="space-y-3">
              {[
                { name: 'Bunda Sarah', action: 'LOGIN', time: '2 menit lalu', color: 'bg-accent' },
                { name: 'Bpk. Ridwan', action: 'REGISTER', time: '10 menit lalu', color: 'bg-primary' },
                { name: 'Ibu Ratna', action: 'LOGOUT', time: '15 menit lalu', color: 'bg-slate-400' },
                { name: 'Mama Arka', action: 'LOGIN', time: '22 menit lalu', color: 'bg-accent' }
              ].map((log, i) => (
                <div key={i} className="flex items-center justify-between p-4 bg-white rounded-3xl border border-slate-50">
                   <div className="flex items-center gap-3">
                      <div className={`w-2 h-2 rounded-full ${log.color} animate-pulse`} />
                      <div>
                        <p className="text-sm font-bold text-slate-800">{log.name}</p>
                        <p className="text-[10px] font-black text-slate-400 uppercase">{log.action}</p>
                      </div>
                   </div>
                   <span className="text-[10px] font-medium text-slate-300">{log.time}</span>
                </div>
              ))}
           </div>
        </section>
      </motion.div>
    );
  }

  if (activeTab === 'payment') {
    return (
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-8">
        <h2 className="text-2xl font-black text-brand-blue tracking-tight">Keuangan Klinik 💰</h2>
        <div className="card-bubble bg-brand-blue text-white overflow-hidden relative">
           <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-10 -mt-10" />
           <p className="text-[10px] font-black uppercase tracking-widest opacity-60">Total Pendapatan (Mei)</p>
           <h3 className="text-3xl font-black mt-2 tracking-tighter">Rp 245.890.000</h3>
           <div className="mt-4 flex gap-2">
              <span className="px-2 py-0.5 bg-white/20 rounded-full text-[9px] font-bold">1,240 Transaksi</span>
           </div>
        </div>

        <div className="space-y-4">
          <div className="flex justify-between items-center px-1">
             <h3 className="font-bold text-slate-700">Tagihan Pending</h3>
             <button className="text-primary text-[10px] font-black uppercase">Refresh</button>
          </div>
          {[1,2,3].map(i => (
            <div key={i} className="card-bubble flex items-center justify-between p-4 bg-white/50 border-slate-50">
               <div>
                 <h4 className="font-bold text-sm">#INV-1029{i}</h4>
                 <p className="text-[10px] text-slate-400 font-bold tracking-tighter">Pasien: Arka Pratama</p>
               </div>
               <div className="text-right">
                  <p className="text-sm font-black text-secondary">Rp 250.000</p>
                  <button className="text-[10px] font-black text-primary uppercase mt-1">Ingatkan</button>
               </div>
            </div>
          ))}
        </div>
      </motion.div>
    );
  }

  if (activeTab === 'settings') {
    return (
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="p-6 space-y-8 pb-32">
        <h2 className="text-2xl font-black text-brand-blue tracking-tight">Data Master & CS 🛠️</h2>

        <div className="grid grid-cols-2 gap-4">
           {[
             { label: 'Pertanyaan Parent', icon: Home, count: '5 Baru', color: 'bg-primary' },
             { label: 'Manage Subs', icon: CreditCard, count: '820 Aktif', color: 'bg-secondary' },
           ].map((card, i) => (
             <button key={i} className="card-bubble text-left space-y-3 p-4 hover:border-primary/30">
                <div className={`w-10 h-10 ${card.color} rounded-xl flex items-center justify-center text-white`}><card.icon size={20} /></div>
                <div>
                   <p className="text-[10px] font-black text-slate-400 uppercase tracking-tight leading-none mb-1">{card.label}</p>
                   <p className="font-bold text-brand-blue text-xs">{card.count}</p>
                </div>
             </button>
           ))}
        </div>

        <section className="space-y-4">
           <div className="flex justify-between items-center px-1">
              <h3 className="font-bold text-slate-700">Tiket Pertanyaan</h3>
              <span className="text-[9px] bg-secondary/10 text-secondary px-2 py-0.5 rounded-full font-black">HIGH PRIORITY</span>
           </div>
           <div className="space-y-4">
              {[
                { user: 'Bunda Sarah', q: 'Bagaimana cara mengganti jadwal imunisasi yang terlewat?', time: '10:30' },
                { user: 'Papa Anto', q: 'Apakah ada spesialis gizi yang buka di hari Minggu?', time: '09:12' }
              ].map((ticket, i) => (
                <div key={i} className="card-bubble border-slate-50 space-y-3 p-4">
                   <div className="flex justify-between">
                      <span className="text-[10px] font-black text-primary uppercase">{ticket.user}</span>
                      <span className="text-[9px] text-slate-300 font-bold">{ticket.time}</span>
                   </div>
                   <p className="text-xs text-slate-600 font-medium italic">"{ticket.q}"</p>
                   <button className="w-full py-2 bg-slate-50 rounded-xl text-[10px] font-black text-slate-500 uppercase tracking-widest hover:bg-primary/10 hover:text-primary transition-all">Balas Sekarang</button>
                </div>
              ))}
           </div>
        </section>

        <section className="space-y-4">
           <h3 className="font-bold text-slate-700">Kelola Akun Parent</h3>
           <div className="card-bubble border-slate-100 p-0 overflow-hidden divide-y divide-slate-50">
             {[
               { name: 'Sarah Meilani', plan: 'Premium', status: 'ACTIVE' },
               { name: 'Budi Hartono', plan: 'Basic', status: 'EXPIRED' }
             ].map((user, i) => (
               <div key={i} className="p-4 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                     <div className="w-9 h-9 bg-slate-100 rounded-full flex items-center justify-center text-slate-400"><User size={18} /></div>
                     <div>
                        <p className="text-sm font-bold text-slate-700">{user.name}</p>
                        <p className="text-[10px] font-medium text-slate-400">{user.plan} Account</p>
                     </div>
                  </div>
                  <div className="text-[9px] font-black uppercase text-accent border border-accent/20 px-2 py-0.5 rounded-full">{user.status}</div>
               </div>
             ))}
           </div>
           <button className="w-full py-4 text-xs font-black text-primary uppercase tracking-widest bg-primary/5 rounded-2xl">Lihat Semua Akun</button>
        </section>
      </motion.div>
    );
  }

  return null;
}
