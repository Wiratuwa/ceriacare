/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
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
  AlertCircle,
  Trash2,
  Check,
  X,
  FileText,
  DollarSign,
  Activity
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

// Types
type Role = 'PARENT' | 'DOCTOR' | 'ADMIN';

interface Child {
  id: string;
  name: string;
  age: string;
  gender: string;
}

interface Parent {
  id: string;
  name: string;
  email: string;
  memberType: string;
}

interface Doctor {
  id: string;
  name: string;
  specialty: string;
}

interface Service {
  id: string;
  name: string;
  price: number;
}

interface Booking {
  id: string;
  parentId: string;
  parentName: string;
  childName: string;
  serviceName: string;
  doctorName: string;
  date: string;
  time: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

interface QueueItem {
  id: string;
  no: string;
  childName: string;
  doctorName: string;
  time: string;
  status: 'Waiting' | 'Examining' | 'Completed';
  patientId: string;
  parentId: string;
}

interface MedicalRecord {
  id: string;
  childName: string;
  parentId: string;
  doctorName: string;
  serviceName: string;
  date: string;
  notes: string;
  height?: string;
  weight?: string;
  prescription: { name: string; dosage: string }[];
}

interface Invoice {
  id: string;
  parentId: string;
  childName: string;
  item: string;
  price: number;
  status: 'PENDING' | 'PENDING_CONFIRMATION' | 'PAID';
}

export default function App() {
  const [role, setRole] = useState<Role | null>(null);
  const [activeTab, setActiveTab] = useState('home');
  const [isRegistering, setIsRegistering] = useState(false);

  // --- Reactive Shared State (Clinic Database Simulator) ---
  const [parents, setParents] = useState<Parent[]>([
    { id: 'parent-1', name: 'Bunda Sarah', email: 'sarah@ceria.com', memberType: 'Premium Member' },
    { id: 'parent-2', name: 'Bpk. Ridwan', email: 'ridwan@ceria.com', memberType: 'Basic Member' }
  ]);

  const [currentParentId, setCurrentParentId] = useState('parent-1');
  const [currentDoctorId, setCurrentDoctorId] = useState('doc-1');
  const [currentAdminId, setCurrentAdminId] = useState('admin-1');
  const [admins] = useState([
    { id: 'admin-1', name: 'Admin CeriaCare' },
    { id: 'admin-2', name: 'Admin Wiratuwa' }
  ]);

  const [childrenDb, setChildrenDb] = useState<Record<string, Child[]>>({
    'parent-1': [
      { id: 'child-1', name: 'Arka Pratama', age: '5 tahun', gender: 'Laki-laki' },
      { id: 'child-2', name: 'Ziva Putri', age: '2 tahun', gender: 'Perempuan' }
    ],
    'parent-2': [
      { id: 'child-3', name: 'Siska Amelia', age: '3 tahun', gender: 'Perempuan' }
    ]
  });

  const [doctors, setDoctors] = useState<Doctor[]>([
    { id: 'doc-1', name: 'dr. Sarah Wijaya, Sp.A', specialty: 'Poli Tumbuh Kembang' },
    { id: 'doc-2', name: 'dr. Budi Santoso, Sp.A', specialty: 'Poli Anak Umum' },
    { id: 'doc-3', name: 'dr. Rian Pratama, Sp.A', specialty: 'Spesialis Gizi Anak' }
  ]);

  const [services, setServices] = useState<Service[]>([
    { id: 'srv-1', name: 'Konsultasi Umum', price: 150000 },
    { id: 'srv-2', name: 'Tumbuh Kembang', price: 250000 },
    { id: 'srv-3', name: 'Vaksinasi DPT', price: 450000 }
  ]);

  const [bookings, setBookings] = useState<Booking[]>([
    {
      id: 'book-1',
      parentId: 'parent-1',
      parentName: 'Bunda Sarah',
      childName: 'Arka Pratama',
      serviceName: 'Tumbuh Kembang',
      doctorName: 'dr. Sarah Wijaya, Sp.A',
      date: '2026-05-22',
      time: '10:15',
      status: 'APPROVED'
    },
    {
      id: 'book-2',
      parentId: 'parent-2',
      parentName: 'Bpk. Ridwan',
      childName: 'Siska Amelia',
      serviceName: 'Konsultasi Umum',
      doctorName: 'dr. Budi Santoso, Sp.A',
      date: '2026-05-22',
      time: '09:45',
      status: 'APPROVED'
    }
  ]);

  const [queue, setQueue] = useState<QueueItem[]>([
    {
      id: 'q-1',
      no: 'A-12',
      childName: 'Arka Pratama',
      doctorName: 'dr. Sarah Wijaya, Sp.A',
      time: '10:15',
      status: 'Examining',
      patientId: 'child-1',
      parentId: 'parent-1'
    },
    {
      id: 'q-2',
      no: 'A-13',
      childName: 'Siska Amelia',
      doctorName: 'dr. Budi Santoso, Sp.A',
      time: '09:45',
      status: 'Waiting',
      patientId: 'child-3',
      parentId: 'parent-2'
    }
  ]);

  const [medicalRecords, setMedicalRecords] = useState<MedicalRecord[]>([
    {
      id: 'mr-1',
      childName: 'Arka Pratama',
      parentId: 'parent-1',
      doctorName: 'dr. Sarah Wijaya, Sp.A',
      serviceName: 'Poli Tumbuh Kembang',
      date: '10 April 2026',
      height: '110 cm',
      weight: '18 kg',
      notes: 'Tumbuh kembang sangat baik, tinggi bertambah 2cm. Disarankan suplemen vitamin D3 harian.',
      prescription: [
        { name: 'Vitamin D3 Drops', dosage: '1x1 drop sehari setelah makan' },
        { name: 'Sangobion Kids Syrup', dosage: '1x1 sendok teh' }
      ]
    }
  ]);

  const [invoices, setInvoices] = useState<Invoice[]>([
    {
      id: 'INV-009',
      parentId: 'parent-1',
      childName: 'Arka Pratama',
      item: 'Pemeriksaan Tumbuh Kembang - Arka',
      price: 250000,
      status: 'PENDING'
    },
    {
      id: 'INV-008',
      parentId: 'parent-1',
      childName: 'Ziva Putri',
      item: 'Vaksin DPT + Konsultasi - Ziva',
      price: 450000,
      status: 'PAID'
    }
  ]);

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

  const handleParentLogin = (parentId: string) => {
    setCurrentParentId(parentId);
    setRole('PARENT');
    setActiveTab('home');
  };

  const handleDoctorLogin = (doctorId: string) => {
    setCurrentDoctorId(doctorId);
    setRole('DOCTOR');
    setActiveTab('home');
  };

  const handleAdminLogin = (adminId: string) => {
    setCurrentAdminId(adminId);
    setRole('ADMIN');
    setActiveTab('home');
  };

  const handleRegisterParent = (name: string, email: string) => {
    const newId = `parent-${parents.length + 1}`;
    const newParent: Parent = {
      id: newId,
      name,
      email,
      memberType: 'Basic Member'
    };
    setParents([...parents, newParent]);
    setChildrenDb({
      ...childrenDb,
      [newId]: []
    });
    setCurrentParentId(newId);
    setRole('PARENT');
    setActiveTab('home');
    setIsRegistering(false);
  };

  if (!role) {
    return (
      <SmartphoneShell>
        {isRegistering ? (
          <RegisterView 
            onRegister={handleRegisterParent} 
            onBack={() => setIsRegistering(false)} 
          />
        ) : (
          <LoginView 
            parents={parents}
            doctors={doctors}
            onLogin={handleParentLogin} 
            onDoctorLogin={handleDoctorLogin}
            onAdminLogin={handleAdminLogin}
            onGoToRegister={() => setIsRegistering(true)}
          />
        )}
      </SmartphoneShell>
    );
  }

  // Get active parent info
  const activeParent = parents.find(p => p.id === currentParentId) || parents[0];

  return (
    <SmartphoneShell>
      {/* Role Switcher overlay for preview ease */}
      <div className="fixed top-6 right-6 z-50 flex gap-2 bg-white p-2 rounded-full shadow-2xl border border-slate-100 group">
        <button onClick={() => setRole(null)} className="p-2 hover:bg-slate-100 rounded-full text-slate-400 transition-colors"><LogOut size={16} /></button>
        <div className="h-4 w-[1px] bg-slate-200 my-auto" />
        <button
          onClick={() => handleParentLogin('parent-1')}
          className={`px-4 py-1.5 rounded-full text-[10px] font-bold transition-all ${
            role === 'PARENT' ? 'text-white shadow-lg' : 'text-slate-400 hover:text-slate-600'
          }`}
          style={role === 'PARENT' ? { backgroundColor: '#4FB6E1' } : {}}
        >
          PARENT
        </button>
        <button
          onClick={() => { setRole('DOCTOR'); setActiveTab('home'); }}
          className={`px-4 py-1.5 rounded-full text-[10px] font-bold transition-all ${
            role === 'DOCTOR' ? 'text-white shadow-lg' : 'text-slate-400 hover:text-slate-600'
          }`}
          style={role === 'DOCTOR' ? { backgroundColor: '#FF8B64' } : {}}
        >
          DOCTOR
        </button>
        <button
          onClick={() => { setRole('ADMIN'); setActiveTab('home'); }}
          className={`px-4 py-1.5 rounded-full text-[10px] font-bold transition-all ${
            role === 'ADMIN' ? 'text-white shadow-lg' : 'text-slate-400 hover:text-slate-600'
          }`}
          style={role === 'ADMIN' ? { backgroundColor: '#76D191' } : {}}
        >
          ADMIN
        </button>
      </div>

      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header - Fixed Height */}
        <header className="px-6 pt-14 pb-8 rounded-b-[45px] shadow-lg relative overflow-hidden flex-shrink-0" style={{ backgroundColor: role === 'DOCTOR' ? '#FF8B64' : role === 'ADMIN' ? '#76D191' : '#4FB6E1' }}>
          <div className="absolute top-[-30px] right-[-30px] w-48 h-48 bg-white/10 rounded-full" />
          <div className="relative z-10 flex justify-between items-center text-white">
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 bg-white/20 backdrop-blur-md rounded-2xl flex items-center justify-center border border-white/30">
                <Stethoscope size={22} className="text-white" />
              </div>
              <div>
                <p className="text-sky-100 text-[10px] font-bold uppercase tracking-widest opacity-80">CeriaCare App</p>
                <h1 className="font-bold text-xl leading-tight tracking-tight font-calligraphy">
                  {role === 'PARENT' ? activeParent.name : role === 'DOCTOR' ? (doctors.find(d => d.id === currentDoctorId)?.name || 'dr. Budi, Sp.A') : (admins.find(a => a.id === currentAdminId)?.name || 'Admin CeriaCare')}
                </h1>
              </div>
            </div>
            <div className="relative">
              <button className="w-11 h-11 bg-white/20 backdrop-blur-md rounded-full flex items-center justify-center border border-white/30">
                <Bell size={20} />
              </button>
            </div>
          </div>
          
          {activeTab === 'home' && role === 'PARENT' && (
            <motion.div 
              initial={{ y: 20, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              className="mt-6 glass-card p-4 flex items-center gap-4 text-white"
            >
              <div className="w-12 h-12 bg-sun rounded-2xl flex items-center justify-center shadow-inner text-brand-blue">
                <Clock size={24} />
              </div>
              <div className="flex-1">
                <p className="text-[10px] text-sky-50 font-black uppercase tracking-widest opacity-95">Jadwal Terdekat</p>
                <p className="text-sm font-bold">
                  {queue.some(q => q.parentId === currentParentId && q.status !== 'Completed')
                    ? `Antrian: ${queue.find(q => q.parentId === currentParentId && q.status !== 'Completed')?.childName} (${queue.find(q => q.parentId === currentParentId && q.status !== 'Completed')?.no})`
                    : 'Tidak ada jadwal terdekat'}
                </p>
                <p className="text-[11px] opacity-80">
                  {queue.some(q => q.parentId === currentParentId && q.status !== 'Completed')
                    ? 'Silakan monitor antrian live di bawah.'
                    : 'Silakan booking di menu Booking.'}
                </p>
              </div>
            </motion.div>
          )}
        </header>

        {/* Dynamic Main Body */}
        <main className="flex-1 overflow-y-auto bg-[#F8FAFC] pb-24 scrollbar-hide">
          <AnimatePresence mode="wait">
            {role === 'PARENT' && (
              <ParentView 
                key="parent" 
                activeTab={activeTab} 
                setActiveTab={setActiveTab} 
                currentParentId={currentParentId}
                activeParent={activeParent}
                childrenDb={childrenDb}
                setChildrenDb={setChildrenDb}
                doctors={doctors}
                services={services}
                bookings={bookings}
                setBookings={setBookings}
                queue={queue}
                medicalRecords={medicalRecords}
                invoices={invoices}
                setInvoices={setInvoices}
                onLogout={() => setRole(null)}
              />
            )}
            {role === 'DOCTOR' && (
              <DoctorView 
                key="doctor" 
                activeTab={activeTab} 
                setActiveTab={setActiveTab} 
                queue={queue}
                setQueue={setQueue}
                medicalRecords={medicalRecords}
                setMedicalRecords={setMedicalRecords}
                invoices={invoices}
                setInvoices={setInvoices}
                onLogout={() => setRole(null)}
                currentDoctorName={doctors.find(d => d.id === currentDoctorId)?.name || 'dr. Budi Santoso, Sp.A'}
              />
            )}
            {role === 'ADMIN' && (
              <AdminView 
                key="admin" 
                activeTab={activeTab} 
                setActiveTab={setActiveTab} 
                bookings={bookings}
                setBookings={setBookings}
                queue={queue}
                setQueue={setQueue}
                invoices={invoices}
                setInvoices={setInvoices}
                parents={parents}
                doctors={doctors}
                setDoctors={setDoctors}
                services={services}
                setServices={setServices}
                onLogout={() => setRole(null)}
                currentAdminName={admins.find(a => a.id === currentAdminId)?.name || 'Admin CeriaCare'}
              />
            )}
          </AnimatePresence>
        </main>

        {/* Unified Bottom Nav */}
        <nav className="absolute bottom-0 left-0 right-0 bg-white border-t border-slate-50 px-8 py-4 flex justify-between items-center rounded-t-[45px] shadow-[0_-15px_40px_rgba(0,0,0,0.04)] z-30 h-24">
          <NavItems role={role} activeTab={activeTab} setActiveTab={setActiveTab} themeColor={role === 'DOCTOR' ? '#FF8B64' : role === 'ADMIN' ? '#76D191' : '#4FB6E1'} />
        </nav>
      </div>
    </SmartphoneShell>
  );
}

function NavButton({ active, icon: Icon, label, onClick, themeColor = '#4FB6E1' }: { active: boolean, icon: any, label: string, onClick: () => void, themeColor?: string }) {
  return (
    <button 
      onClick={onClick}
      className={`flex flex-col items-center gap-1 transition-all ${active ? '' : 'text-slate-400'}`}
      style={active ? { color: themeColor } : {}}
    >
      <div className="p-2 rounded-2xl transition-all" style={active ? { backgroundColor: `${themeColor}18` } : {}}>
        <Icon size={24} strokeWidth={active ? 2.5 : 2} />
      </div>
      <span className="text-[10px] font-bold uppercase tracking-wide">{label}</span>
      {active && <motion.div layoutId="nav-dot" className="w-1 h-1 rounded-full" style={{ backgroundColor: themeColor }} />}
    </button>
  );
}

function NavItems({ role, activeTab, setActiveTab, themeColor }: { role: Role, activeTab: string, setActiveTab: (t: string) => void, themeColor: string }) {
  if (role === 'PARENT') return (
    <>
      <NavButton active={activeTab === 'home'} icon={Home} label="Home" onClick={() => setActiveTab('home')} themeColor={themeColor} />
      <NavButton active={activeTab === 'booking'} icon={Calendar} label="Booking" onClick={() => setActiveTab('booking')} themeColor={themeColor} />
      <NavButton active={activeTab === 'history'} icon={History} label="Riwayat" onClick={() => setActiveTab('history')} themeColor={themeColor} />
      <NavButton active={activeTab === 'payment'} icon={CreditCard} label="Bayar" onClick={() => setActiveTab('payment')} themeColor={themeColor} />
      <NavButton active={activeTab === 'profile'} icon={User} label="Profil" onClick={() => setActiveTab('profile')} themeColor={themeColor} />
    </>
  );
  if (role === 'DOCTOR') return (
    <>
      <NavButton active={activeTab === 'home'} icon={Home} label="Antrian" onClick={() => setActiveTab('home')} themeColor={themeColor} />
      <NavButton active={activeTab === 'history'} icon={History} label="Selesai" onClick={() => setActiveTab('history')} themeColor={themeColor} />
      <NavButton active={activeTab === 'profile'} icon={User} label="Profil" onClick={() => setActiveTab('profile')} themeColor={themeColor} />
    </>
  );
  return (
    <>
      <NavButton active={activeTab === 'home'} icon={Home} label="Monitor" onClick={() => setActiveTab('home')} themeColor={themeColor} />
      <NavButton active={activeTab === 'payment'} icon={CreditCard} label="Keuangan" onClick={() => setActiveTab('payment')} themeColor={themeColor} />
      <NavButton active={activeTab === 'settings'} icon={Settings} label="Master" onClick={() => setActiveTab('settings')} themeColor={themeColor} />
    </>
  );
}

function LoginView({ 
  parents, 
  doctors,
  onLogin, 
  onDoctorLogin, 
  onAdminLogin, 
  onGoToRegister 
}: { 
  parents: Parent[], 
  doctors: Doctor[],
  onLogin: (id: string) => void, 
  onDoctorLogin: (id: string) => void, 
  onAdminLogin: (id: string) => void, 
  onGoToRegister: () => void 
}) {
  const [selectedParentId, setSelectedParentId] = useState(parents[0]?.id || '');
  const [selectedDoctorId, setSelectedDoctorId] = useState(doctors[0]?.id || '');
  const [selectedAdminId, setSelectedAdminId] = useState('admin-1');
  const [loginMode, setLoginMode] = useState<'PARENT' | 'DOCTOR' | 'ADMIN'>('PARENT');

  const admins = [
    { id: 'admin-1', name: 'Admin CeriaCare' },
    { id: 'admin-2', name: 'Admin Wiratuwa' }
  ];

  const selectedParent = parents.find(p => p.id === selectedParentId) || parents[0];
  const selectedDoctor = doctors.find(d => d.id === selectedDoctorId) || doctors[0];
  const selectedAdmin = admins.find(a => a.id === selectedAdminId) || admins[0];

  const themes: Record<string, { color: string; label: string; softBg: string; activeBorder: string }> = {
    PARENT: { color: '#4FB6E1', label: selectedParent ? selectedParent.name : 'Bunda', softBg: '#E0F2FE', activeBorder: '#4FB6E1' },
    DOCTOR: { color: '#FF8B64', label: selectedDoctor ? selectedDoctor.name : 'dr. Budi, Sp.A', softBg: '#FFF7ED', activeBorder: '#FF8B64' },
    ADMIN:  { color: '#76D191', label: selectedAdmin ? selectedAdmin.name : 'Admin CeriaCare', softBg: '#F0FDF4', activeBorder: '#76D191' },
  };

  const theme = themes[loginMode];

  const handleLogin = () => {
    if (loginMode === 'PARENT') onLogin(selectedParentId);
    else if (loginMode === 'DOCTOR') onDoctorLogin(selectedDoctorId);
    else onAdminLogin(selectedAdminId);
  };

  const handleRoleToggle = (mode: 'DOCTOR' | 'ADMIN') => {
    setLoginMode(prev => prev === mode ? 'PARENT' : mode);
  };

  return (
    <div className="p-8 flex flex-col items-center justify-center text-center h-full bg-white space-y-8 relative overflow-hidden">
      {/* Animated background glow */}
      <motion.div 
        animate={{ backgroundColor: theme.color }}
        transition={{ duration: 0.6 }}
        className="absolute top-[-10%] right-[-10%] w-64 h-64 rounded-full blur-3xl opacity-[0.06]" 
      />
      <motion.div 
        animate={{ backgroundColor: theme.color }}
        transition={{ duration: 0.8 }}
        className="absolute bottom-[-5%] left-[-15%] w-48 h-48 rounded-full blur-3xl opacity-[0.04]" 
      />
      
      {/* Animated Logo */}
      <motion.div 
        initial={{ scale: 0.5, opacity: 0 }}
        animate={{ 
          scale: 1, 
          opacity: 1, 
          backgroundColor: theme.color,
          boxShadow: `0 25px 50px -12px ${theme.color}33`
        }}
        transition={{ type: 'spring', stiffness: 200, damping: 20 }}
        className="w-28 h-28 rounded-[40px] flex items-center justify-center text-white relative z-10"
      >
        <div className="absolute inset-2 border-2 border-white/20 rounded-[30px]" />
        <Stethoscope size={54} strokeWidth={2.5} />
      </motion.div>

      <div className="space-y-2 z-10">
        <h1 className="text-4xl font-black text-brand-blue tracking-tighter font-calligraphy">CeriaCare</h1>
        <p className="text-slate-400 font-bold uppercase tracking-[0.3em] text-[9px]">Pediatric Care Redefined</p>
      </div>

      <div className="w-full space-y-4 pt-4 z-10">
        {/* Stable layout account selector preventing jumping UI */}
        <div className="space-y-1.5 text-left pb-2">
          <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1 block h-4 truncate whitespace-nowrap">
            {loginMode === 'PARENT' ? 'Pilih Akun Bunda/Wali' : loginMode === 'DOCTOR' ? 'Pilih Akun Dokter' : 'Pilih Akun Admin'}
          </label>
          <motion.div
            animate={{
              borderColor: theme.color,
              boxShadow: `0 0 0 2px ${theme.color}15`
            }}
            transition={{ duration: 0.3 }}
            className="rounded-2xl border bg-slate-50 overflow-hidden relative"
          >
            <select 
              value={loginMode === 'PARENT' ? selectedParentId : loginMode === 'DOCTOR' ? selectedDoctorId : selectedAdminId}
              onChange={(e) => {
                if (loginMode === 'PARENT') setSelectedParentId(e.target.value);
                else if (loginMode === 'DOCTOR') setSelectedDoctorId(e.target.value);
                else setSelectedAdminId(e.target.value);
              }}
              className="w-full h-14 pl-4 pr-12 bg-transparent font-medium text-slate-700 outline-none transition-all focus:bg-white appearance-none cursor-pointer truncate"
            >
              {loginMode === 'PARENT' && parents.map((parent) => (
                <option key={parent.id} value={parent.id}>{parent.name} ({parent.email})</option>
              ))}
              {loginMode === 'DOCTOR' && doctors.map((doc) => (
                <option key={doc.id} value={doc.id}>{doc.name} ({doc.specialty})</option>
              ))}
              {loginMode === 'ADMIN' && admins.map((admin) => (
                <option key={admin.id} value={admin.id}>{admin.name}</option>
              ))}
            </select>
            <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-400">
              <ChevronRight size={18} className="rotate-95" />
            </div>
          </motion.div>
        </div>

        {/* Main login button — animated color morph */}
        <motion.button 
          onClick={handleLogin}
          animate={{ 
            backgroundColor: theme.color,
            boxShadow: `0 10px 25px -5px ${theme.color}40`
          }}
          transition={{ type: 'spring', stiffness: 200, damping: 25 }}
          className="w-full h-14 text-xs tracking-wider flex items-center justify-center gap-3 text-white font-bold rounded-full uppercase active:scale-[0.98] cursor-pointer"
        >
          <AnimatePresence mode="wait">
            <motion.span
              key={loginMode}
              initial={{ y: 10, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: -10, opacity: 0 }}
              transition={{ duration: 0.18 }}
              className="flex items-center justify-center gap-2 whitespace-nowrap truncate px-4 max-w-full"
            >
              Masuk Sebagai {theme.label} <ChevronRight size={16} />
            </motion.span>
          </AnimatePresence>
        </motion.button>

        {/* Role toggle buttons */}
        <div className="flex gap-3 pt-2">
          <motion.button 
            onClick={() => {
              if (loginMode === 'DOCTOR') {
                setLoginMode('PARENT');
              } else {
                setLoginMode('DOCTOR');
              }
            }}
            animate={{
              backgroundColor: loginMode === 'DOCTOR' ? '#E0F2FE' : '#F8FAFC',
              borderColor: loginMode === 'DOCTOR' ? '#4FB6E1' : '#F1F5F9',
              scale: loginMode === 'DOCTOR' ? 1.03 : 1,
            }}
            transition={{ type: 'spring', stiffness: 300, damping: 25 }}
            className="flex-1 h-[76px] rounded-2xl font-bold border flex flex-col items-center justify-center gap-1.5 cursor-pointer"
          >
            {loginMode === 'DOCTOR' ? (
              <>
                <Home size={20} style={{ color: '#4FB6E1' }} />
                <span className="text-[9px] uppercase tracking-wider font-extrabold" style={{ color: '#4FB6E1' }}>Bunda / Wali</span>
              </>
            ) : (
              <>
                <User size={20} style={{ color: '#FF8B64' }} />
                <span className="text-[9px] uppercase tracking-wider text-slate-600">Dokter Anak</span>
              </>
            )}
          </motion.button>
          
          <motion.button 
            onClick={() => {
              if (loginMode === 'ADMIN') {
                setLoginMode('PARENT');
              } else {
                setLoginMode('ADMIN');
              }
            }}
            animate={{
              backgroundColor: loginMode === 'ADMIN' ? '#E0F2FE' : '#F8FAFC',
              borderColor: loginMode === 'ADMIN' ? '#4FB6E1' : '#F1F5F9',
              scale: loginMode === 'ADMIN' ? 1.03 : 1,
            }}
            transition={{ type: 'spring', stiffness: 300, damping: 25 }}
            className="flex-1 h-[76px] rounded-2xl font-bold border flex flex-col items-center justify-center gap-1.5 cursor-pointer"
          >
            {loginMode === 'ADMIN' ? (
              <>
                <Home size={20} style={{ color: '#4FB6E1' }} />
                <span className="text-[9px] uppercase tracking-wider font-extrabold" style={{ color: '#4FB6E1' }}>Bunda / Wali</span>
              </>
            ) : (
              <>
                <Settings size={20} style={{ color: '#76D191' }} />
                <span className="text-[9px] uppercase tracking-wider text-slate-600">Admin Klinik</span>
              </>
            )}
          </motion.button>
        </div>
      </div>
      
      <div className="pt-6 text-[8px] font-black uppercase text-slate-300 tracking-[0.3em]">Official Clinic Management System</div>
      
      <div className="pt-2 h-6 flex items-center justify-center z-10">
        <motion.button 
          onClick={onGoToRegister} 
          animate={{ opacity: loginMode === 'PARENT' ? 1 : 0 }}
          style={{ pointerEvents: loginMode === 'PARENT' ? 'auto' : 'none' }}
          className="text-primary text-[10px] font-bold uppercase tracking-widest hover:underline transition-all"
        >
          Registrasi Akun Baru
        </motion.button>
      </div>
    </div>
  );
}

function RegisterView({ onRegister, onBack }: { onRegister: (name: string, email: string) => void, onBack: () => void }) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = () => {
    if (!name || !email) {
      alert('Nama dan Email harus diisi');
      return;
    }
    onRegister(name, email);
  };

  return (
    <div className="p-8 flex flex-col items-center justify-center bg-white h-full space-y-6 relative overflow-hidden">
      <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-primary via-secondary to-accent" />
      
      <div className="w-full text-left space-y-2">
        <button onClick={onBack} className="p-2 bg-slate-100 rounded-full mb-2"><ChevronRight className="rotate-180" size={16} /></button>
        <h2 className="text-2xl font-black text-brand-blue tracking-tight">Daftar Akun Baru</h2>
        <p className="text-slate-400 text-xs font-medium">Buat akun CeriaCare untuk monitor kesehatan anak Anda.</p>
      </div>

      <div className="w-full space-y-3">
        <div className="space-y-1">
          <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">Nama Lengkap Ortu/Wali</label>
          <input 
            type="text" 
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full p-3.5 rounded-xl bg-slate-50 border border-slate-100 font-medium outline-none transition-all focus:ring-2 focus:ring-primary/20 focus:bg-white" 
            placeholder="Contoh: Bunda Sarah"
          />
        </div>
        <div className="space-y-1">
          <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">Email</label>
          <input 
            type="email" 
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-3.5 rounded-xl bg-slate-50 border border-slate-100 font-medium outline-none transition-all focus:ring-2 focus:ring-primary/20 focus:bg-white" 
            placeholder="sarah@example.com"
          />
        </div>
        <div className="space-y-1">
          <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">Password</label>
          <input 
            type="password" 
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3.5 rounded-xl bg-slate-50 border border-slate-100 font-medium outline-none transition-all focus:ring-2 focus:ring-primary/20 focus:bg-white" 
            placeholder="••••••••"
          />
        </div>
      </div>

      <div className="w-full space-y-3 pt-2">
        <button onClick={handleSubmit} className="w-full btn-primary h-12 text-xs tracking-widest flex items-center justify-center gap-2">
          Daftar Sekarang <Plus size={16} />
        </button>
        <p className="text-center text-[9px] text-slate-400 font-medium leading-relaxed">
          Dengan mendaftar, Anda menyetujui <span className="text-primary font-bold">Syarat & Ketentuan</span> kami.
        </p>
      </div>
    </div>
  );
}

// --- PARENT VIEW ---
function ParentView({ 
  activeTab, 
  setActiveTab, 
  currentParentId,
  activeParent,
  childrenDb,
  setChildrenDb,
  doctors,
  services,
  bookings,
  setBookings,
  queue,
  medicalRecords,
  invoices,
  setInvoices,
  onLogout
}: { 
  activeTab: string, 
  setActiveTab: (t: string) => void,
  currentParentId: string,
  activeParent: Parent,
  childrenDb: Record<string, Child[]>,
  setChildrenDb: React.Dispatch<React.SetStateAction<Record<string, Child[]>>>,
  doctors: Doctor[],
  services: Service[],
  bookings: Booking[],
  setBookings: React.Dispatch<React.SetStateAction<Booking[]>>,
  queue: QueueItem[],
  medicalRecords: MedicalRecord[],
  invoices: Invoice[],
  setInvoices: React.Dispatch<React.SetStateAction<Invoice[]>>,
  onLogout: () => void,
  key?: React.Key
}) {
  const myChildren = childrenDb[currentParentId] || [];
  
  // States for adding child modal
  const [showAddChildModal, setShowAddChildModal] = useState(false);
  const [newChildName, setNewChildName] = useState('');
  const [newChildAge, setNewChildAge] = useState('');
  const [newChildGender, setNewChildGender] = useState('Laki-laki');

  // Booking states
  const [selectedChildName, setSelectedChildName] = useState('');
  const [selectedServiceId, setSelectedServiceId] = useState(services[0]?.id || '');
  const [selectedDoctorId, setSelectedDoctorId] = useState(doctors[0]?.id || '');
  const [bookingDate, setBookingDate] = useState('2026-05-22');
  const [bookingTime, setBookingTime] = useState('09:00');
  const [showBookingSuccess, setShowBookingSuccess] = useState(false);

  // Active checkup detail modal
  const [selectedRecord, setSelectedRecord] = useState<MedicalRecord | null>(null);

  // Payment modal state
  const [payingInvoice, setPayingInvoice] = useState<Invoice | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('VA');
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);
  const [showPaymentSuccess, setShowPaymentSuccess] = useState(false);

  // Initialize booking form default child
  React.useEffect(() => {
    if (myChildren.length > 0 && !selectedChildName) {
      setSelectedChildName(myChildren[0].name);
    }
  }, [myChildren, selectedChildName]);

  const handleAddChild = () => {
    if (!newChildName || !newChildAge) {
      alert('Nama dan Umur anak harus diisi.');
      return;
    }
    const newChild: Child = {
      id: `child-${Date.now()}`,
      name: newChildName,
      age: `${newChildAge} tahun`,
      gender: newChildGender
    };
    
    setChildrenDb({
      ...childrenDb,
      [currentParentId]: [...myChildren, newChild]
    });
    setSelectedChildName(newChild.name);
    
    // reset form
    setNewChildName('');
    setNewChildAge('');
    setNewChildGender('Laki-laki');
    setShowAddChildModal(false);
  };

  const handleCreateBooking = () => {
    if (!selectedChildName) {
      alert('Silakan pilih anak terlebih dahulu. Jika belum ada anak, daftarkan anak baru.');
      return;
    }
    const selectedService = services.find(s => s.id === selectedServiceId) || services[0];
    const selectedDoctor = doctors.find(d => d.id === selectedDoctorId) || doctors[0];

    const newBooking: Booking = {
      id: `book-${Date.now()}`,
      parentId: currentParentId,
      parentName: activeParent.name,
      childName: selectedChildName,
      serviceName: selectedService.name,
      doctorName: selectedDoctor.name,
      date: bookingDate,
      time: bookingTime,
      status: 'PENDING'
    };

    setBookings([newBooking, ...bookings]);
    setShowBookingSuccess(true);
  };

  const handlePayInvoice = () => {
    if (!payingInvoice) return;
    setIsProcessingPayment(true);
    
    setTimeout(() => {
      // update invoice state
      setInvoices(invoices.map(inv => 
        inv.id === payingInvoice.id 
          ? { ...inv, status: 'PENDING_CONFIRMATION' as const } 
          : inv
      ));
      setIsProcessingPayment(false);
      setShowPaymentSuccess(true);
      setPayingInvoice(null);
    }, 1500);
  };

  // Filter lists for active parent
  const myBookings = bookings.filter(b => b.parentId === currentParentId);
  const myQueueItems = queue.filter(q => q.parentId === currentParentId);
  const myInvoices = invoices.filter(inv => inv.parentId === currentParentId);
  const myMedicalRecords = medicalRecords.filter(mr => mr.parentId === currentParentId);

  // BOOKING TAB
  if (activeTab === 'booking') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-black text-brand-blue">Booking Jadwal</h2>
          <button 
            onClick={() => setShowAddChildModal(true)} 
            className="flex items-center gap-1 bg-secondary/15 text-secondary font-bold text-xs px-3 py-1.5 rounded-full hover:bg-secondary/20 transition-all"
          >
            <Plus size={14} /> Anak Baru
          </button>
        </div>

        <div className="card-bubble space-y-5 shadow-lg">
          <div className="space-y-2">
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Pilih Anak (Pasien)</label>
            {myChildren.length === 0 ? (
              <div className="p-4 border-2 border-dashed border-slate-200 rounded-2xl text-center space-y-2">
                <p className="text-xs text-slate-400 font-medium">Belum ada anak terdaftar di akun Anda.</p>
                <button 
                  onClick={() => setShowAddChildModal(true)} 
                  className="text-primary font-bold text-xs uppercase tracking-wider hover:underline"
                >
                  + Daftarkan Anak Baru
                </button>
              </div>
            ) : (
              <div className="flex flex-wrap gap-2">
                {myChildren.map((c) => (
                  <button 
                    key={c.id} 
                    onClick={() => setSelectedChildName(c.name)}
                    className={`px-4 py-2 rounded-full text-xs font-bold transition-all ${
                      selectedChildName === c.name 
                        ? 'bg-primary text-white shadow-md shadow-primary/20' 
                        : 'bg-slate-50 text-slate-500 border border-slate-100 hover:bg-slate-100'
                    }`}
                  >
                    {c.name}
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="space-y-4">
            <div className="space-y-1">
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Pilih Layanan</label>
              <select 
                value={selectedServiceId}
                onChange={(e) => setSelectedServiceId(e.target.value)}
                className="w-full p-4 bg-slate-50 rounded-2xl font-bold text-slate-600 border border-slate-100 outline-none"
              >
                {services.map(s => (
                  <option key={s.id} value={s.id}>{s.name} (Rp {s.price.toLocaleString()})</option>
                ))}
              </select>
            </div>
            
            <div className="space-y-1">
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Pilih Dokter</label>
              <select 
                value={selectedDoctorId}
                onChange={(e) => setSelectedDoctorId(e.target.value)}
                className="w-full p-4 bg-slate-50 rounded-2xl font-bold text-slate-600 border border-slate-100 outline-none"
              >
                {doctors.map(d => (
                  <option key={d.id} value={d.id}>{d.name} • {d.specialty}</option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Tanggal</label>
                <input 
                  type="date" 
                  value={bookingDate}
                  onChange={(e) => setBookingDate(e.target.value)}
                  className="w-full p-3.5 bg-slate-50 rounded-2xl font-bold text-slate-600 border border-slate-100 outline-none text-xs"
                />
              </div>
              <div className="space-y-1">
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Jam Estimasi</label>
                <input 
                  type="time" 
                  value={bookingTime}
                  onChange={(e) => setBookingTime(e.target.value)}
                  className="w-full p-3.5 bg-slate-50 rounded-2xl font-bold text-slate-600 border border-slate-100 outline-none text-xs"
                />
              </div>
            </div>
          </div>

          <button onClick={handleCreateBooking} className="w-full btn-primary h-14 mt-2">Konfirmasi Jadwal</button>
        </div>

        {/* Existing Booking Requests */}
        <section className="space-y-3">
          <h3 className="font-black text-xs text-brand-blue uppercase tracking-widest ml-1">Status Permintaan Booking</h3>
          {myBookings.length === 0 ? (
            <div className="card-bubble p-6 text-center text-slate-400 text-xs font-medium">Belum ada pemesanan jadwal.</div>
          ) : (
            <div className="space-y-3">
              {myBookings.map((b) => (
                <div key={b.id} className="card-bubble border-slate-100 flex items-center justify-between p-4 bg-white">
                  <div className="space-y-1">
                    <p className="text-xs font-bold text-slate-800">{b.childName} - {b.serviceName}</p>
                    <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{b.doctorName} • {b.date} {b.time}</p>
                  </div>
                  <span className={`text-[8px] font-black px-2 py-1 rounded-full uppercase tracking-wider ${
                    b.status === 'APPROVED' ? 'bg-accent/10 text-accent' : 
                    b.status === 'REJECTED' ? 'bg-red-100 text-red-600' : 'bg-yellow-100 text-yellow-600'
                  }`}>
                    {b.status}
                  </span>
                </div>
              ))}
            </div>
          )}
        </section>

        {/* Modal: Registrasi Pasien Baru */}
        <AnimatePresence>
          {showAddChildModal && (
            <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-6">
              <motion.div 
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                className="bg-white rounded-[35px] w-full max-w-[360px] p-6 space-y-4 shadow-2xl relative"
              >
                <button onClick={() => setShowAddChildModal(false)} className="absolute top-4 right-4 p-2 bg-slate-100 hover:bg-slate-200 rounded-full transition-colors text-slate-400">
                  <X size={16} />
                </button>
                <div className="flex items-center gap-3 pt-2">
                  <div className="p-3 bg-secondary/10 rounded-2xl text-secondary"><User size={24} /></div>
                  <div>
                    <h3 className="font-black text-brand-blue text-lg">Registrasi Pasien Baru</h3>
                    <p className="text-[10px] text-slate-400 font-medium">Tambahkan profil anak Anda ke sistem.</p>
                  </div>
                </div>

                <div className="space-y-3">
                  <div className="space-y-1">
                    <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">Nama Lengkap Anak</label>
                    <input 
                      type="text" 
                      value={newChildName}
                      onChange={(e) => setNewChildName(e.target.value)}
                      placeholder="Contoh: Arka Pratama"
                      className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl text-xs font-semibold outline-none focus:bg-white"
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="space-y-1">
                      <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">Umur (Tahun)</label>
                      <input 
                        type="number" 
                        value={newChildAge}
                        onChange={(e) => setNewChildAge(e.target.value)}
                        placeholder="Contoh: 5"
                        className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl text-xs font-semibold outline-none focus:bg-white"
                      />
                    </div>
                    <div className="space-y-1">
                      <label className="text-[9px] font-bold text-slate-400 uppercase tracking-widest ml-1">Jenis Kelamin</label>
                      <select 
                        value={newChildGender}
                        onChange={(e) => setNewChildGender(e.target.value)}
                        className="w-full p-3 bg-slate-50 border border-slate-100 rounded-xl text-xs font-semibold outline-none"
                      >
                        <option value="Laki-laki">Laki-laki</option>
                        <option value="Perempuan">Perempuan</option>
                      </select>
                    </div>
                  </div>
                </div>

                <button onClick={handleAddChild} className="w-full btn-secondary h-12 text-xs font-black uppercase tracking-widest shadow-md">
                  Daftarkan Anak
                </button>
              </motion.div>
            </div>
          )}
        </AnimatePresence>

        {/* Modal: Booking Success Alert */}
        <AnimatePresence>
          {showBookingSuccess && (
            <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-6">
              <motion.div 
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                className="bg-white rounded-[35px] w-full max-w-[340px] p-6 text-center space-y-4 shadow-2xl"
              >
                <div className="w-16 h-16 bg-accent/15 text-accent rounded-full flex items-center justify-center mx-auto shadow-inner">
                  <CheckCircle2 size={36} />
                </div>
                <div>
                  <h3 className="font-black text-brand-blue text-lg">Booking Berhasil</h3>
                  <p className="text-xs text-slate-400 font-medium leading-relaxed mt-1">Permintaan pendaftaran berhasil dikirim. Admin akan segera meninjau dan memberikan nomor antrian.</p>
                </div>
                <button onClick={() => setShowBookingSuccess(false)} className="w-full btn-primary h-12 text-xs">Oke, Mengerti</button>
              </motion.div>
            </div>
          )}
        </AnimatePresence>
      </motion.div>
    );
  }

  // PAYMENT TAB
  if (activeTab === 'payment') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
        <h2 className="text-2xl font-black text-brand-blue">Pembayaran</h2>
        
        <div className="space-y-4">
          {myInvoices.length === 0 ? (
            <div className="card-bubble p-8 text-center text-slate-400 text-xs font-medium">Tidak ada invoice untuk akun Anda.</div>
          ) : (
            myInvoices.map((inv) => (
              <div key={inv.id} className="card-bubble border-slate-100">
                <div className="flex justify-between items-start mb-2">
                  <span className="text-[10px] font-mono text-slate-400 font-bold uppercase tracking-wider">{inv.id}</span>
                  <span className={`text-[8px] font-black px-2.5 py-1 rounded-full uppercase tracking-wider ${
                    inv.status === 'PAID' ? 'bg-accent/10 text-accent' : 
                    inv.status === 'PENDING_CONFIRMATION' ? 'bg-blue-50 text-blue-600' : 'bg-secondary/10 text-secondary'
                  }`}>
                    {inv.status === 'PENDING' ? 'BELUM BAYAR' : 
                     inv.status === 'PENDING_CONFIRMATION' ? 'MENUNGGU VERIFIKASI' : 'LUNAS'}
                  </span>
                </div>
                <h4 className="font-bold text-slate-700">{inv.item}</h4>
                <p className="text-lg font-black text-brand-blue mt-1">Rp {inv.price.toLocaleString()}</p>
                
                {inv.status === 'PENDING' && (
                  <button 
                    onClick={() => setPayingInvoice(inv)}
                    className="w-full mt-4 py-3 bg-secondary text-white rounded-2xl font-bold text-xs uppercase tracking-widest shadow-lg shadow-secondary/20 active:scale-[0.98] transition-all"
                  >
                    Bayar Sekarang
                  </button>
                )}
              </div>
            ))
          )}
        </div>

        {/* Modal Payment Processing */}
        <AnimatePresence>
          {payingInvoice && (
            <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-6">
              <motion.div 
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                className="bg-white rounded-[35px] w-full max-w-[360px] p-6 space-y-4 shadow-2xl relative"
              >
                <button onClick={() => setPayingInvoice(null)} className="absolute top-4 right-4 p-2 bg-slate-100 hover:bg-slate-200 rounded-full transition-colors text-slate-400">
                  <X size={16} />
                </button>
                
                <div className="pt-2 text-center">
                  <h3 className="font-black text-brand-blue text-lg">Pilih Metode Pembayaran</h3>
                  <p className="text-[11px] text-slate-400 font-medium">Selesaikan tagihan untuk {payingInvoice.childName}</p>
                  <p className="text-xl font-black text-secondary mt-1">Rp {payingInvoice.price.toLocaleString()}</p>
                </div>

                <div className="space-y-2.5">
                  {[
                    { id: 'VA', name: 'Virtual Account Bank', desc: 'BCA, Mandiri, BNI, BRI' },
                    { id: 'QRIS', name: 'QRIS / E-Wallet', desc: 'GoPay, OVO, ShopeePay' }
                  ].map(method => (
                    <button 
                      key={method.id} 
                      onClick={() => setPaymentMethod(method.id)}
                      className={`w-full p-4 rounded-2xl text-left border flex items-center justify-between transition-all ${
                        paymentMethod === method.id 
                          ? 'border-primary bg-primary/5 ring-1 ring-primary' 
                          : 'border-slate-100 bg-slate-50 hover:bg-slate-100'
                      }`}
                    >
                      <div>
                        <p className="text-xs font-bold text-slate-800">{method.name}</p>
                        <p className="text-[10px] text-slate-400 font-medium">{method.desc}</p>
                      </div>
                      <div className={`w-5 h-5 rounded-full border flex items-center justify-center ${
                        paymentMethod === method.id ? 'border-primary bg-primary text-white' : 'border-slate-300'
                      }`}>
                        {paymentMethod === method.id && <Check size={12} />}
                      </div>
                    </button>
                  ))}
                </div>

                <button 
                  onClick={handlePayInvoice} 
                  disabled={isProcessingPayment}
                  className="w-full btn-primary h-12 text-xs font-black uppercase tracking-widest flex items-center justify-center gap-2"
                >
                  {isProcessingPayment ? (
                    <>Memproses...</>
                  ) : (
                    <>Konfirmasi Pembayaran</>
                  )}
                </button>
              </motion.div>
            </div>
          )}
        </AnimatePresence>

        {/* Modal: Payment success Alert */}
        <AnimatePresence>
          {showPaymentSuccess && (
            <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-6">
              <motion.div 
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                className="bg-white rounded-[35px] w-full max-w-[340px] p-6 text-center space-y-4 shadow-2xl"
              >
                <div className="w-16 h-16 bg-accent/15 text-accent rounded-full flex items-center justify-center mx-auto shadow-inner">
                  <CheckCircle2 size={36} />
                </div>
                <div>
                  <h3 className="font-black text-brand-blue text-lg">Pembayaran Terkirim</h3>
                  <p className="text-xs text-slate-400 font-medium leading-relaxed mt-1">Pembayaran telah dikirim ke sistem. Silakan tunggu konfirmasi pembayaran oleh Admin Klinik.</p>
                </div>
                <button onClick={() => setShowPaymentSuccess(false)} className="w-full btn-primary h-12 text-xs">Tutup</button>
              </motion.div>
            </div>
          )}
        </AnimatePresence>
      </motion.div>
    );
  }

  // RIWAYAT TAB
  if (activeTab === 'history') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
        <h2 className="text-2xl font-bold text-brand-blue">Riwayat Rekam Medis</h2>
        <div className="space-y-4">
          {myMedicalRecords.length === 0 ? (
            <div className="card-bubble p-8 text-center text-slate-400 text-xs font-medium">Belum ada riwayat rekam medis.</div>
          ) : (
            myMedicalRecords.map((mr, i) => (
              <motion.div 
                initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: i * 0.08, ease: [0.22, 1, 0.36, 1] }}
                key={mr.id} 
                className="card-bubble space-y-4 border-slate-100 bg-white shadow-sm"
              >
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-bold text-base text-slate-800">{mr.childName} ({mr.serviceName})</h3>
                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">{mr.date} • {mr.doctorName}</p>
                  </div>
                  <div className="bg-accent/10 text-accent px-2 py-0.5 rounded-lg text-[9px] font-black uppercase tracking-wider">Selesai</div>
                </div>
                
                <div className="bg-slate-50 p-4 rounded-2xl flex items-start gap-3">
                  <FileText size={18} className="text-slate-400 mt-0.5" />
                  <div className="text-xs font-medium text-slate-600 leading-relaxed">
                    <p className="font-bold text-slate-700">Hasil Observasi:</p>
                    <p className="italic">"{mr.notes}"</p>
                  </div>
                </div>

                <div className="flex gap-2">
                  <button 
                    onClick={() => setSelectedRecord(mr)}
                    className="flex-1 py-2.5 text-xs font-black uppercase tracking-widest text-primary bg-primary/10 rounded-xl hover:bg-primary/20 transition-colors"
                  >
                    Detail Resep & Fisik
                  </button>
                </div>
              </motion.div>
            ))
          )}
        </div>

        {/* Modal Checkup detail */}
        <AnimatePresence>
          {selectedRecord && (
            <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-6">
              <motion.div 
                initial={{ scale: 0.9, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.9, opacity: 0 }}
                className="bg-white rounded-[35px] w-full max-w-[360px] p-6 space-y-4 shadow-2xl relative"
              >
                <button onClick={() => setSelectedRecord(null)} className="absolute top-4 right-4 p-2 bg-slate-100 hover:bg-slate-200 rounded-full transition-colors text-slate-400">
                  <X size={16} />
                </button>
                
                <div className="flex items-center gap-3 pt-2">
                  <div className="p-3 bg-primary/10 rounded-2xl text-primary"><Activity size={24} /></div>
                  <div>
                    <h3 className="font-black text-brand-blue text-base">Detail Pemeriksaan</h3>
                    <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{selectedRecord.childName} • {selectedRecord.date}</p>
                  </div>
                </div>

                <div className="space-y-3 bg-slate-50 p-4 rounded-2xl text-xs font-semibold text-slate-600">
                  <div className="grid grid-cols-2 gap-2 border-b border-slate-100 pb-2">
                    <p className="text-slate-400 uppercase text-[9px] tracking-wider font-bold">Tinggi Badan</p>
                    <p className="text-slate-700 text-right">{selectedRecord.height || '-'}</p>
                    <p className="text-slate-400 uppercase text-[9px] tracking-wider font-bold">Berat Badan</p>
                    <p className="text-slate-700 text-right">{selectedRecord.weight || '-'}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-slate-400 uppercase text-[9px] tracking-wider font-bold">Hasil Observasi / Diagnosa</p>
                    <p className="text-slate-700 italic font-medium leading-relaxed">"{selectedRecord.notes}"</p>
                  </div>
                </div>

                <div className="space-y-2">
                  <h4 className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">Resep Obat (Prescription)</h4>
                  {selectedRecord.prescription.length === 0 ? (
                    <p className="text-xs text-slate-400 italic ml-1">Tidak ada resep obat.</p>
                  ) : (
                    <div className="space-y-2">
                      {selectedRecord.prescription.map((med, idx) => (
                        <div key={idx} className="p-3 bg-slate-50 border border-slate-100 rounded-xl flex items-center justify-between">
                          <p className="text-xs font-bold text-slate-800">{med.name}</p>
                          <p className="text-[10px] text-primary font-black">{med.dosage}</p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                <button onClick={() => setSelectedRecord(null)} className="w-full btn-primary h-12 text-xs">Tutup</button>
              </motion.div>
            </div>
          )}
        </AnimatePresence>
      </motion.div>
    );
  }

  // HOME TAB (MONITOR STATUS ANTRIAN)
  if (activeTab === 'home') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
        {/* Services Grid */}
        <section>
          <h3 className="text-brand-blue font-black text-xs uppercase tracking-[0.2em] mb-4">Layanan Utama</h3>
          <div className="grid grid-cols-4 gap-4">
            {[
              { icon: <Calendar size={20} />, label: 'Booking', t: 'booking', bg: 'bg-primary/10 border-primary/20', color: 'text-primary' },
              { icon: <History size={20} />, label: 'Riwayat', t: 'history', bg: 'bg-accent/10 border-accent/20', color: 'text-accent' },
              { icon: <CreditCard size={20} />, label: 'Tagihan', t: 'payment', bg: 'bg-secondary/10 border-secondary/20', color: 'text-secondary' },
              { icon: <User size={20} />, label: 'Profil', t: 'profile', bg: 'bg-sun/10 border-sun/20', color: 'text-brand-blue' }
            ].map((item, i) => (
              <button key={i} onClick={() => setActiveTab(item.t)} className="flex flex-col items-center gap-2 group">
                <div className={`w-14 h-14 ${item.bg} ${item.color} border rounded-2xl flex items-center justify-center shadow-sm group-hover:scale-105 active:scale-95 transition-all`}>
                  {item.icon}
                </div>
                <span className="text-[9px] font-black text-slate-500 uppercase tracking-wider">{item.label}</span>
              </button>
            ))}
          </div>
        </section>

        {/* Live Queue Progress Monitor */}
        <section className="space-y-3">
          <div className="flex justify-between items-center px-1">
            <h3 className="font-black text-sm text-brand-blue uppercase tracking-widest">Pantau Antrian Anda</h3>
            <span className="bg-accent/15 text-accent text-[9px] font-black px-2 py-0.5 rounded-full animate-pulse">LIVE</span>
          </div>

          {myQueueItems.filter(q => q.status !== 'Completed').length === 0 ? (
            <div className="card-bubble p-6 text-center text-slate-400 text-xs font-medium">
              Tidak ada anak Anda dalam antrian aktif hari ini.
              <button onClick={() => setActiveTab('booking')} className="block text-primary font-black uppercase text-[10px] tracking-wider mt-2 mx-auto hover:underline">
                Booking Antrian Sekarang →
              </button>
            </div>
          ) : (
            myQueueItems.filter(q => q.status !== 'Completed').map((item, i) => (
              <motion.div 
                initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, delay: i * 0.08, ease: [0.22, 1, 0.36, 1] }}
                key={item.id} 
                className="card-bubble border-primary/10"
              >
                <div className="flex items-center gap-5">
                  <div className="w-16 h-16 bg-primary/10 rounded-2xl flex flex-col items-center justify-center border border-primary/20">
                    <span className="text-[9px] font-black text-primary/70 uppercase">NO</span>
                    <span className="text-xl font-black text-brand-blue">{item.no}</span>
                  </div>
                  <div className="flex-1 space-y-1.5">
                    <div>
                      <h5 className="font-black text-slate-800 text-sm">{item.childName}</h5>
                      <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{item.doctorName}</p>
                    </div>
                    <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden">
                      <motion.div 
                        initial={{ width: 0 }} 
                        animate={{ width: item.status === 'Examining' ? '75%' : '25%' }} 
                        className={`h-full ${item.status === 'Examining' ? 'bg-secondary' : 'bg-primary'}`} 
                      />
                    </div>
                    <p className={`text-[9px] font-black text-right ${
                      item.status === 'Examining' ? 'text-secondary' : 'text-primary'
                    }`}>
                      {item.status === 'Examining' ? 'Sedang Diperiksa Dokter' : 'Dalam Antrian Tunggu'}
                    </p>
                  </div>
                </div>
              </motion.div>
            ))
          )}
        </section>

        {/* Custom Care Banner */}
        <div className="bg-[#FDE68A]/30 border border-sun/30 rounded-[30px] p-5 relative overflow-hidden flex items-center justify-between">
          <div className="space-y-1 pr-6 flex-1">
            <h4 className="text-[#92400E] font-black text-base">Konsultasi Darurat</h4>
            <p className="text-[10px] text-[#92400E]/70 font-medium leading-relaxed">Punya pertanyaan medis darurat? Hubungi staf kami langsung.</p>
            <button className="mt-2.5 bg-secondary text-white px-4 py-1.5 rounded-full text-[9px] font-black uppercase tracking-widest shadow-md">
              Hubungi CS
            </button>
          </div>
          <div className="w-14 h-14 bg-white/70 rounded-2xl flex items-center justify-center shadow-inner text-primary flex-shrink-0">
            <Bell size={24} />
          </div>
        </div>
      </motion.div>
    );
  }

  // PROFILE TAB
  if (activeTab === 'profile') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6 pb-24">
        <div className="flex flex-col items-center gap-3 pt-4">
           <div className="w-20 h-20 rounded-full border-4 border-white shadow-xl overflow-hidden bg-primary/20 flex items-center justify-center text-primary">
              <User size={40} />
           </div>
           <div className="text-center">
              <h2 className="text-2xl font-black text-brand-blue font-calligraphy">{activeParent.name}</h2>
              <p className="text-slate-400 text-[10px] font-bold uppercase tracking-widest">{activeParent.memberType}</p>
           </div>
        </div>

        <div className="space-y-3">
          <h3 className="font-black text-[9px] text-slate-400 uppercase tracking-widest ml-2">Profil Anak Anda</h3>
          <div className="grid grid-cols-2 gap-3">
             {myChildren.length === 0 ? (
               <div className="col-span-2 card-bubble p-4 text-center text-slate-400 text-xs">Belum ada anak terdaftar.</div>
             ) : (
               myChildren.map(c => (
                 <div key={c.id} className="card-bubble p-4 border-slate-50 flex flex-col items-center text-center gap-1.5">
                    <div className="w-10 h-10 bg-primary/10 text-primary rounded-full flex items-center justify-center">
                      <User size={18} />
                    </div>
                    <div>
                      <span className="text-xs font-black text-slate-700 block">{c.name}</span>
                      <span className="text-[9px] text-slate-400 font-bold block mt-0.5">{c.age} • {c.gender}</span>
                    </div>
                 </div>
               ))
             )}
          </div>
        </div>

        <div className="space-y-2">
          <h3 className="font-black text-[9px] text-slate-400 uppercase tracking-widest ml-2">Akun & Sesi</h3>
          <button 
            onClick={onLogout} 
            className="card-bubble w-full flex items-center justify-between border-red-50 bg-red-50/20 py-4"
          >
             <div className="flex items-center gap-4">
                <LogOut size={20} className="text-red-400" />
                <span className="font-bold text-red-600 text-xs uppercase tracking-wider">Keluar Akun (Logout)</span>
             </div>
             <ChevronRight size={18} className="text-red-200" />
          </button>
        </div>
      </motion.div>
    );
  }

  return null;
}

// --- DOCTOR VIEW ---
function DoctorView({ 
  activeTab, 
  setActiveTab, 
  queue, 
  setQueue,
  medicalRecords,
  setMedicalRecords,
  invoices,
  setInvoices,
  onLogout,
  currentDoctorName
}: { 
  activeTab: string, 
  setActiveTab: (t: string) => void,
  queue: QueueItem[],
  setQueue: React.Dispatch<React.SetStateAction<QueueItem[]>>,
  medicalRecords: MedicalRecord[],
  setMedicalRecords: React.Dispatch<React.SetStateAction<MedicalRecord[]>>,
  invoices: Invoice[],
  setInvoices: React.Dispatch<React.SetStateAction<Invoice[]>>,
  onLogout: () => void,
  currentDoctorName: string,
  key?: React.Key
}) {
  const [selectedPatient, setSelectedPatient] = useState<QueueItem | null>(null);

  // New checkup form states
  const [heightInput, setHeightInput] = useState('');
  const [weightInput, setWeightInput] = useState('');
  const [diagnosisInput, setDiagnosisInput] = useState('');
  const [medNameInput, setMedNameInput] = useState('');
  const [medDosageInput, setMedDosageInput] = useState('');
  const [tempPrescription, setTempPrescription] = useState<{ name: string; dosage: string }[]>([]);

  const handleAddMedicine = () => {
    if (!medNameInput || !medDosageInput) {
      alert('Nama obat dan dosis harus diisi');
      return;
    }
    setTempPrescription([...tempPrescription, { name: medNameInput, dosage: medDosageInput }]);
    setMedNameInput('');
    setMedDosageInput('');
  };

  const handleRemoveTempMed = (idx: number) => {
    setTempPrescription(tempPrescription.filter((_, i) => i !== idx));
  };

  const handleStartExam = (patient: QueueItem) => {
    setSelectedPatient(patient);
    setQueue(queue.map(q => q.id === patient.id ? { ...q, status: 'Examining' as const } : q));
  };

  const handleSaveExamResult = () => {
    if (!selectedPatient) return;
    if (!diagnosisInput) {
      alert('Tuliskan diagnosa atau hasil observasi terlebih dahulu.');
      return;
    }

    // 1. Add medical record
    const newRecord: MedicalRecord = {
      id: `mr-${Date.now()}`,
      childName: selectedPatient.childName,
      parentId: selectedPatient.parentId,
      doctorName: selectedPatient.doctorName,
      serviceName: 'Konsultasi & Pemeriksaan',
      date: new Date().toLocaleDateString('id-ID', { day: 'numeric', month: 'long', year: 'numeric' }),
      height: heightInput ? `${heightInput} cm` : undefined,
      weight: weightInput ? `${weightInput} kg` : undefined,
      notes: diagnosisInput,
      prescription: tempPrescription
    };
    setMedicalRecords([newRecord, ...medicalRecords]);

    // 2. Generate Invoice
    const newInvoice: Invoice = {
      id: `INV-${Date.now().toString().slice(-4)}`,
      parentId: selectedPatient.parentId,
      childName: selectedPatient.childName,
      item: `Konsultasi & Pemeriksaan - ${selectedPatient.childName}`,
      price: 150000 + (tempPrescription.length * 50000), // Base fee + drugs estimation
      status: 'PENDING'
    };
    setInvoices([newInvoice, ...invoices]);

    // 3. Mark Queue Item as Completed
    setQueue(queue.map(q => q.id === selectedPatient.id ? { ...q, status: 'Completed' as const } : q));

    // Reset checkup form state
    setHeightInput('');
    setWeightInput('');
    setDiagnosisInput('');
    setTempPrescription([]);
    setSelectedPatient(null);
    alert('Pemeriksaan berhasil disimpan, Rekam medis terupdate, tagihan terbuat.');
  };

  // ACTIVE CHECKUP SCREEN (MENCATAT HASIL & MEMBUAT RESEP & MELIHAT REKAM MEDIS)
  if (selectedPatient) {
    const childHistory = medicalRecords.filter(mr => mr.childName === selectedPatient.childName);

    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6 pb-28">
        <div className="flex items-center gap-3">
          <button onClick={() => setSelectedPatient(null)} className="p-2 bg-white rounded-xl shadow-sm"><ChevronRight className="rotate-180" size={16} /></button>
          <div>
            <h2 className="text-xl font-black text-[#E05E2E]">{selectedPatient.childName}</h2>
            <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Antrian {selectedPatient.no} • {selectedPatient.time}</p>
          </div>
        </div>

        <div className="space-y-5">
          {/* Patient History View */}
          <div className="card-bubble border-slate-100 space-y-3 bg-slate-50">
             <h3 className="font-black text-slate-700 text-xs uppercase tracking-wider flex items-center gap-2"><History size={16} /> Riwayat Medis Pasien</h3>
             {childHistory.length === 0 ? (
               <p className="text-xs text-slate-400 italic">Belum ada riwayat kontrol terdahulu.</p>
             ) : (
               <div className="max-h-[120px] overflow-y-auto space-y-2 pr-1">
                 {childHistory.map((hist, idx) => (
                   <div key={idx} className="bg-white p-2.5 rounded-xl border border-slate-100 text-[11px] leading-relaxed">
                     <p className="font-bold text-slate-700">{hist.date} - {hist.doctorName}</p>
                     <p className="text-slate-500 mt-0.5">Obs: "{hist.notes}"</p>
                   </div>
                 ))}
               </div>
             )}
          </div>

          {/* New Checkup inputs */}
          <div className="card-bubble border-slate-100 space-y-4">
             <h3 className="font-black text-slate-800 text-xs uppercase tracking-wider">Pencatatan Pemeriksaan</h3>
             
             <div className="grid grid-cols-2 gap-3">
               <div className="space-y-1">
                 <label className="text-[9px] font-bold text-slate-400 uppercase">Tinggi Badan (cm)</label>
                 <input 
                   type="number" 
                   value={heightInput}
                   onChange={(e) => setHeightInput(e.target.value)}
                   className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none focus:bg-white" 
                   placeholder="Contoh: 110" 
                 />
               </div>
               <div className="space-y-1">
                 <label className="text-[9px] font-bold text-slate-400 uppercase">Berat Badan (kg)</label>
                 <input 
                   type="number" 
                   value={weightInput}
                   onChange={(e) => setWeightInput(e.target.value)}
                   className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none focus:bg-white" 
                   placeholder="Contoh: 18" 
                 />
               </div>
             </div>

             <div className="space-y-1">
               <label className="text-[9px] font-bold text-slate-400 uppercase">Keluhan & Hasil Observasi</label>
               <textarea 
                 value={diagnosisInput}
                 onChange={(e) => setDiagnosisInput(e.target.value)}
                 className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none min-h-[80px]" 
                 placeholder="Tulis diagnosa dokter..." 
               />
             </div>

             {/* Prescription Builder */}
             <div className="space-y-3 pt-2 border-t border-slate-50">
               <label className="text-[9px] font-black text-slate-400 uppercase tracking-widest">Membuat Resep Obat</label>
               
               <div className="flex flex-col gap-2">
                  <div className="flex gap-2">
                    <input 
                      type="text"
                      value={medNameInput}
                      onChange={(e) => setMedNameInput(e.target.value)}
                      className="flex-1 p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none focus:bg-white" 
                      placeholder="Nama Obat" 
                    />
                    <input 
                      type="text"
                      value={medDosageInput}
                      onChange={(e) => setMedDosageInput(e.target.value)}
                      className="w-[110px] p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none focus:bg-white" 
                      placeholder="Dosis (e.g. 3x1)" 
                    />
                  </div>
                  <button 
                    onClick={handleAddMedicine} 
                    className="w-full py-2.5 bg-secondary hover:bg-secondary/95 text-white font-bold text-xs rounded-xl shadow-md shadow-secondary/10 flex items-center justify-center gap-1.5"
                  >
                    <Plus size={14} /> Tambah ke Resep
                  </button>
                </div>

               {/* Temp Prescription List */}
               {tempPrescription.length > 0 && (
                 <div className="space-y-2 bg-slate-50 p-2.5 rounded-xl border border-slate-100">
                   {tempPrescription.map((med, idx) => (
                     <div key={idx} className="flex items-center justify-between text-xs bg-white p-2 rounded-lg border border-slate-50">
                       <span className="font-bold text-slate-700">{med.name} ({med.dosage})</span>
                       <button onClick={() => handleRemoveTempMed(idx)} className="text-red-400 hover:text-red-600"><Trash2 size={14} /></button>
                     </div>
                   ))}
                 </div>
               )}
             </div>

             <button onClick={handleSaveExamResult} className="w-full h-12 mt-2 text-white font-bold py-3.5 px-6 rounded-full shadow-lg active:scale-[0.98] transition-all text-sm uppercase tracking-wider" style={{ backgroundColor: '#FF8B64', boxShadow: '0 10px 15px -3px rgba(255,139,100,0.25)' }}>Simpan & Selesai</button>
          </div>
        </div>
      </motion.div>
    );
  }

  // FINISHED / HISTORY TAB
  if (activeTab === 'history') {
    const finishedPatients = queue.filter(q => q.status === 'Completed');

    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
        <h2 className="text-2xl font-black text-[#E05E2E]">Selesai Dirawat</h2>
        {finishedPatients.length === 0 ? (
          <div className="card-bubble p-8 text-center text-slate-400 text-xs font-medium">Belum ada pasien selesai dirawat hari ini.</div>
        ) : (
          <div className="space-y-3">
            {finishedPatients.map((p, idx) => (
              <div key={idx} className="card-bubble border-slate-100 flex items-center gap-4 bg-white/60">
                <div className="w-12 h-12 bg-accent/10 rounded-xl flex items-center justify-center text-accent"><CheckCircle2 size={24} /></div>
                <div className="flex-1">
                  <h4 className="font-bold text-slate-800 text-sm">{p.childName}</h4>
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Antrian {p.no} • {p.time}</p>
                </div>
                <span className="text-[8px] font-black uppercase text-accent bg-accent/15 px-2 py-0.5 rounded-full">SELESAI</span>
              </div>
            ))}
          </div>
        )}
      </motion.div>
    );
  }

  // PROFILE TAB
  if (activeTab === 'profile') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
        <div className="flex flex-col items-center gap-3 pt-4">
           <div className="w-20 h-20 rounded-full border-4 border-white shadow-xl overflow-hidden flex items-center justify-center" style={{ backgroundColor: 'rgba(255,139,100,0.2)', color: '#FF8B64' }}>
              <User size={40} />
           </div>
           <div className="text-center">
              <h2 className="text-2xl font-black text-[#E05E2E] font-calligraphy">{currentDoctorName}</h2>
              <p className="text-slate-400 text-[10px] font-bold uppercase tracking-widest">Dokter CeriaCare</p>
           </div>
        </div>

        <div className="space-y-2">
          <h3 className="font-black text-[9px] text-slate-400 uppercase tracking-widest ml-2">Sesi Akun</h3>
          <button 
            onClick={onLogout} 
            className="card-bubble w-full flex items-center justify-between border-red-50 bg-red-50/20 py-4"
          >
             <div className="flex items-center gap-4">
                <LogOut size={20} className="text-red-400" />
                <span className="font-bold text-red-600 text-xs uppercase tracking-wider">Log Out Dokter</span>
             </div>
             <ChevronRight size={18} className="text-red-200" />
          </button>
        </div>
      </motion.div>
    );
  }

  // HOME / LIVE QUEUE TAB
  const waitingPatients = queue.filter(q => q.status !== 'Completed');

  return (
    <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-black text-[#E05E2E]">Antrian Hari Ini</h2>
        <div className="bg-secondary/15 px-3 py-1.5 rounded-full text-secondary font-black text-xs uppercase tracking-wider">
          {waitingPatients.length} Pasien
        </div>
      </div>

      <div className="space-y-3">
        {waitingPatients.length === 0 ? (
          <div className="card-bubble p-8 text-center text-slate-400 text-xs font-medium">Tidak ada antrian pasien menunggu.</div>
        ) : (
          waitingPatients.map((item, i) => (
            <div 
              key={i} 
              onClick={() => handleStartExam(item)}
              className="card-bubble flex items-center justify-between bg-white border-slate-100 hover:border-secondary/30 cursor-pointer group active:scale-[0.99]"
            >
              <div className="flex items-center gap-4">
                <div className="w-14 h-14 bg-slate-50 rounded-2xl flex flex-col items-center justify-center border border-slate-100 group-hover:bg-secondary/10 transition-colors">
                  <span className="text-[9px] font-bold text-slate-400">NO</span>
                  <span className="text-lg font-black text-slate-800">{item.no}</span>
                </div>
                <div>
                  <h4 className="font-bold text-slate-800 text-sm">{item.childName}</h4>
                  <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">
                    {item.status === 'Examining' ? 'Sedang Dilayani' : 'Menunggu'} • Jam {item.time}
                  </p>
                </div>
              </div>
              <button className="bg-secondary text-white text-[9px] font-black uppercase tracking-wider px-4 py-2 rounded-xl shadow-md group-hover:bg-secondary/90">
                {item.status === 'Examining' ? 'LANJUT' : 'LAYANI'}
              </button>
            </div>
          ))
        )}
      </div>
    </motion.div>
  );
}

// --- ADMIN VIEW ---
function AdminView({ 
  activeTab, 
  setActiveTab, 
  bookings, 
  setBookings, 
  queue, 
  setQueue,
  invoices,
  setInvoices,
  parents,
  doctors,
  setDoctors,
  services,
  setServices,
  onLogout,
  currentAdminName
}: { 
  activeTab: string, 
  setActiveTab: (t: string) => void,
  bookings: Booking[],
  setBookings: React.Dispatch<React.SetStateAction<Booking[]>>,
  queue: QueueItem[],
  setQueue: React.Dispatch<React.SetStateAction<QueueItem[]>>,
  invoices: Invoice[],
  setInvoices: React.Dispatch<React.SetStateAction<Invoice[]>>,
  parents: Parent[],
  doctors: Doctor[],
  setDoctors: React.Dispatch<React.SetStateAction<Doctor[]>>,
  services: Service[],
  setServices: React.Dispatch<React.SetStateAction<Service[]>>,
  onLogout: () => void,
  currentAdminName: string,
  key?: React.Key
}) {
  // Master add states
  const [newDocName, setNewDocName] = useState('');
  const [newDocSpec, setNewDocSpec] = useState('');
  const [newSrvName, setNewSrvName] = useState('');
  const [newSrvPrice, setNewSrvPrice] = useState('');

  const handleApproveBooking = (book: Booking) => {
    // 1. Update Booking status to Approved
    setBookings(bookings.map(b => b.id === book.id ? { ...b, status: 'APPROVED' as const } : b));

    // 2. Generate next queue ticket
    const queueLetters = ['A', 'B', 'C'];
    const letter = queueLetters[Math.floor(Math.random() * 3)];
    const ticketNo = `${letter}-${Math.floor(Math.random() * 80) + 10}`;

    // 3. Add to live queue
    const newQueueItem: QueueItem = {
      id: `q-${Date.now()}`,
      no: ticketNo,
      childName: book.childName,
      doctorName: book.doctorName,
      time: book.time,
      status: 'Waiting',
      patientId: `child-${Date.now()}`, // mock patient ID
      parentId: book.parentId
    };

    setQueue([...queue, newQueueItem]);
    alert(`Pemesanan ${book.childName} disetujui! Antrian ${ticketNo} dibuat.`);
  };

  const handleRejectBooking = (bookId: string) => {
    setBookings(bookings.map(b => b.id === bookId ? { ...b, status: 'REJECTED' as const } : b));
  };

  const handleConfirmPayment = (invoiceId: string) => {
    setInvoices(invoices.map(inv => inv.id === invoiceId ? { ...inv, status: 'PAID' as const } : inv));
    alert('Konfirmasi pembayaran invoice berhasil!');
  };

  const handleAddDoctor = () => {
    if (!newDocName || !newDocSpec) {
      alert('Isi Nama dan Spesialisasi Dokter');
      return;
    }
    const newDoc: Doctor = {
      id: `doc-${Date.now()}`,
      name: newDocName,
      specialty: newDocSpec
    };
    setDoctors([...doctors, newDoc]);
    setNewDocName('');
    setNewDocSpec('');
  };

  const handleDeleteDoctor = (docId: string) => {
    setDoctors(doctors.filter(d => d.id !== docId));
  };

  const handleAddService = () => {
    if (!newSrvName || !newSrvPrice) {
      alert('Isi Nama dan Harga Layanan');
      return;
    }
    const newSrv: Service = {
      id: `srv-${Date.now()}`,
      name: newSrvName,
      price: parseInt(newSrvPrice)
    };
    setServices([...services, newSrv]);
    setNewSrvName('');
    setNewSrvPrice('');
  };

  const handleDeleteService = (srvId: string) => {
    setServices(services.filter(s => s.id !== srvId));
  };

  // HOME / MONITOR TAB
  if (activeTab === 'home') {
    const pendingBookings = bookings.filter(b => b.status === 'PENDING');

    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6 pb-24">
        <div className="space-y-1">
           <h2 className="text-xl font-black text-[#2D8A4E] font-calligraphy">Selamat Datang, {currentAdminName}</h2>
           <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Status & Monitor Pendaftaran</p>
        </div>

        {/* Live Statistics */}
        <div className="grid grid-cols-2 gap-3">
           <div className="card-bubble bg-gradient-to-br from-accent/5 to-white flex flex-col p-4">
              <span className="text-[9px] font-black text-accent uppercase">Menunggu Pemeriksaan</span>
              <span className="text-2xl font-black text-[#2D8A4E] mt-1">{queue.filter(q => q.status !== 'Completed').length}</span>
           </div>
           <div className="card-bubble bg-gradient-to-br from-accent/5 to-white flex flex-col p-4">
              <span className="text-[9px] font-black text-accent uppercase">Booking Pending</span>
              <span className="text-2xl font-black text-[#2D8A4E] mt-1">{pendingBookings.length}</span>
           </div>
        </div>

        {/* Booking Requests Management (MENGELOLA PENDAFTARAN) */}
        <section className="space-y-3">
           <h3 className="font-black text-xs text-slate-700 uppercase tracking-wider ml-1">Persetujuan Pendaftaran (Booking)</h3>
           {pendingBookings.length === 0 ? (
             <p className="card-bubble text-center text-slate-400 text-xs py-6 font-medium border-slate-50">Tidak ada permintaan booking pending.</p>
           ) : (
             <div className="space-y-3">
               {pendingBookings.map((book) => (
                 <div key={book.id} className="card-bubble border-slate-100 p-4 bg-white space-y-3">
                   <div className="flex justify-between">
                     <div>
                       <h4 className="font-bold text-sm text-slate-800">{book.childName}</h4>
                       <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">{book.serviceName} • {book.doctorName}</p>
                       <p className="text-[10px] text-slate-400 font-bold uppercase tracking-wider">Jam: {book.time}</p>
                     </div>
                     <span className="text-[8px] bg-yellow-100 text-yellow-600 px-2 py-0.5 rounded-full font-black uppercase h-fit">Pending</span>
                   </div>
                   <div className="flex gap-2">
                     <button 
                       onClick={() => handleApproveBooking(book)}
                        className="flex-1 py-2 text-[10px] font-black text-white rounded-xl uppercase tracking-wider shadow-sm" style={{ backgroundColor: '#76D191' }}
                     >
                       Setujui (Approve)
                     </button>
                     <button 
                       onClick={() => handleRejectBooking(book.id)}
                       className="py-2 px-3 text-[10px] font-black text-slate-400 bg-slate-100 rounded-xl uppercase tracking-wider"
                     >
                       Tolak
                     </button>
                   </div>
                 </div>
               ))}
             </div>
           )}
        </section>
      </motion.div>
    );
  }

  // PAYMENT / TRANSAKSI TAB (MENGELOLA TRANSAKSI & PEMBAYARAN)
  if (activeTab === 'payment') {
    const confirmationInvoices = invoices.filter(inv => inv.status === 'PENDING_CONFIRMATION');
    const unpaidInvoices = invoices.filter(inv => inv.status === 'PENDING');
    const paidInvoices = invoices.filter(inv => inv.status === 'PAID');

    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6 pb-24">
        <h2 className="text-2xl font-black text-[#2D8A4E] tracking-tight">Keuangan & Transaksi</h2>
        
        {/* Invoices Awaiting Confirmation */}
        <section className="space-y-3">
          <h3 className="font-black text-xs text-accent uppercase tracking-wider ml-1">Konfirmasi Pembayaran Ortu</h3>
          {confirmationInvoices.length === 0 ? (
            <p className="card-bubble text-center text-slate-400 text-xs py-6">Tidak ada pembayaran menunggu verifikasi.</p>
          ) : (
            <div className="space-y-3">
              {confirmationInvoices.map((inv) => (
                <div key={inv.id} className="card-bubble flex items-center justify-between p-4" style={{ backgroundColor: 'rgba(118,209,145,0.06)', borderColor: 'rgba(118,209,145,0.25)' }}>
                  <div>
                    <h4 className="font-bold text-sm text-slate-800">{inv.id} - {inv.childName}</h4>
                    <p className="text-[10px] text-slate-500 font-medium">{inv.item}</p>
                    <p className="text-xs font-black text-accent mt-1">Rp {inv.price.toLocaleString()}</p>
                  </div>
                  <button 
                    onClick={() => handleConfirmPayment(inv.id)}
                    className="py-2.5 px-3 text-white font-black text-[9px] uppercase tracking-wider rounded-xl shadow-md active:scale-95 transition-all" style={{ backgroundColor: '#76D191' }}
                  >
                    Konfirmasi
                  </button>
                </div>
              ))}
            </div>
          )}
        </section>

        {/* All Invoices Status */}
        <section className="space-y-2">
          <h3 className="font-black text-xs text-slate-700 uppercase tracking-wider ml-1">Status Seluruh Invoice</h3>
          <div className="card-bubble p-0 overflow-hidden divide-y divide-slate-50">
            {invoices.map((inv) => (
              <div key={inv.id} className="p-4 flex items-center justify-between text-xs">
                <div>
                  <p className="font-bold text-slate-800">{inv.id} • {inv.childName}</p>
                  <p className="text-[10px] text-slate-400">{inv.item}</p>
                </div>
                <div className="text-right">
                  <p className="font-black text-slate-800">Rp {inv.price.toLocaleString()}</p>
                  <span className={`text-[8px] font-black tracking-wider uppercase px-2 py-0.5 rounded-full block w-fit ml-auto mt-1 ${
                    inv.status === 'PAID' ? 'bg-accent/15 text-accent' : 
                    inv.status === 'PENDING_CONFIRMATION' ? 'bg-orange-100 text-orange-600' : 'bg-red-100 text-red-600'
                  }`}>
                    {inv.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </section>
      </motion.div>
    );
  }

  // MASTER DATA TAB (MENGELOLA DATA MASTER)
  if (activeTab === 'settings') {
    return (
      <motion.div initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4, ease: [0.22, 1, 0.36, 1] }} className="p-6 space-y-6 pb-24">
        <h2 className="text-2xl font-black text-[#2D8A4E] tracking-tight">Kelola Data Master</h2>

        {/* Master Dokter */}
        <section className="space-y-3">
          <h3 className="font-black text-xs text-slate-700 uppercase tracking-wider ml-1">Master Dokter Anak</h3>
          
          <div className="card-bubble p-4 space-y-3 border-slate-100">
             <div className="flex flex-col gap-2">
               <input 
                 type="text" 
                 value={newDocName}
                 onChange={(e) => setNewDocName(e.target.value)}
                 className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none transition-all focus:ring-2 focus:ring-accent/20 focus:bg-white" 
                 placeholder="Nama Dokter" 
               />
               <input 
                 type="text" 
                 value={newDocSpec}
                 onChange={(e) => setNewDocSpec(e.target.value)}
                 className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none transition-all focus:ring-2 focus:ring-accent/20 focus:bg-white" 
                 placeholder="Poli/Spesialis" 
               />
               <button 
                 onClick={handleAddDoctor} 
                 className="w-full py-2.5 text-white rounded-xl font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-1.5 shadow-md active:scale-[0.98] transition-all" style={{ backgroundColor: '#76D191', boxShadow: '0 4px 6px -1px rgba(118,209,145,0.2)' }}
               >
                 <Plus size={14} /> Tambah Dokter
               </button>
             </div>

             <div className="space-y-2 pt-2 divide-y divide-slate-50 max-h-[150px] overflow-y-auto pr-1">
               {doctors.map(d => (
                 <div key={d.id} className="flex items-center justify-between text-xs py-2">
                   <div>
                     <p className="font-bold text-slate-800">{d.name}</p>
                     <p className="text-[10px] text-slate-400">{d.specialty}</p>
                   </div>
                   <button onClick={() => handleDeleteDoctor(d.id)} className="text-red-400 hover:text-red-600 p-1"><Trash2 size={14} /></button>
                 </div>
               ))}
             </div>
          </div>
        </section>

        {/* Master Layanan */}
        <section className="space-y-3">
          <h3 className="font-black text-xs text-slate-700 uppercase tracking-wider ml-1">Master Layanan & Harga</h3>
          
          <div className="card-bubble p-4 space-y-3 border-slate-100">
             <div className="flex flex-col gap-2">
               <input 
                 type="text" 
                 value={newSrvName}
                 onChange={(e) => setNewSrvName(e.target.value)}
                 className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none transition-all focus:ring-2 focus:ring-accent/20 focus:bg-white" 
                 placeholder="Nama Layanan" 
               />
               <input 
                 type="number" 
                 value={newSrvPrice}
                 onChange={(e) => setNewSrvPrice(e.target.value)}
                 className="w-full p-3 rounded-xl bg-slate-50 border border-slate-100 text-xs font-semibold outline-none transition-all focus:ring-2 focus:ring-accent/20 focus:bg-white" 
                 placeholder="Harga" 
               />
               <button 
                 onClick={handleAddService} 
                 className="w-full py-2.5 text-white rounded-xl font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-1.5 shadow-md active:scale-[0.98] transition-all" style={{ backgroundColor: '#76D191', boxShadow: '0 4px 6px -1px rgba(118,209,145,0.2)' }}
               >
                 <Plus size={14} /> Tambah Layanan
               </button>
             </div>

             <div className="space-y-2 pt-2 divide-y divide-slate-50 max-h-[150px] overflow-y-auto pr-1">
               {services.map(s => (
                 <div key={s.id} className="flex items-center justify-between text-xs py-2">
                   <div>
                     <p className="font-bold text-slate-800">{s.name}</p>
                     <p className="text-[10px] text-slate-400">Rp {s.price.toLocaleString()}</p>
                   </div>
                   <button onClick={() => handleDeleteService(s.id)} className="text-red-400 hover:text-red-600 p-1"><Trash2 size={14} /></button>
                 </div>
               ))}
             </div>
          </div>
        </section>

        {/* Logout System admin */}
        <div className="pt-2">
          <button 
            onClick={onLogout} 
            className="card-bubble w-full flex items-center justify-between border-red-50 bg-red-50/20 py-4"
          >
             <div className="flex items-center gap-4">
                <LogOut size={20} className="text-red-400" />
                <span className="font-bold text-red-600 text-xs uppercase tracking-wider">Log Out Admin</span>
             </div>
             <ChevronRight size={18} className="text-red-200" />
          </button>
        </div>
      </motion.div>
    );
  }

  return null;
}
