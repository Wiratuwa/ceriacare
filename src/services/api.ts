/**
 * CeriaCare Centralized API Client Service
 * 
 * This service handles all async requests to the PHP/MySQL backend API.
 * It reads the VITE_API_BASE_URL environment variable and provides structured,
 * type-safe functions for CRUD operations matching the clinic use cases.
 */

// --- TypeScript Interfaces ---
export type Role = 'PARENT' | 'DOCTOR' | 'ADMIN';

export interface Child {
  id: string;
  name: string;
  age: string;
  gender: string;
}

export interface Parent {
  id: string;
  name: string;
  email: string;
  memberType: string;
}

export interface Doctor {
  id: string;
  name: string;
  specialty: string;
}

export interface Service {
  id: string;
  name: string;
  price: number;
}

export interface Booking {
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

export interface QueueItem {
  id: string;
  no: string;
  childName: string;
  doctorName: string;
  time: string;
  status: 'Waiting' | 'Examining' | 'Completed';
  patientId: string;
  parentId: string;
}

export interface MedicalRecord {
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

export interface Invoice {
  id: string;
  parentId: string;
  childName: string;
  item: string;
  price: number;
  status: 'PENDING' | 'PENDING_CONFIRMATION' | 'PAID';
}

// --- API Client Settings ---
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost/ceriacare-api';

/**
 * Generic Fetch Wrapper with JSON and CORS support
 */
async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${API_BASE_URL}/${endpoint}`;
  
  const headers = new Headers({
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...(options.headers || {}),
  });

  // Attach token from localStorage if implementing JWT auth later
  const token = localStorage.getItem('ceriacare_token');
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const config: RequestInit = {
    ...options,
    headers,
  };

  try {
    const response = await fetch(url, config);
    
    // Check for HTTP errors
    if (!response.ok) {
      const errorText = await response.text();
      let errorJson;
      try {
        errorJson = JSON.parse(errorText);
      } catch (e) {
        // Response was not JSON
      }
      throw new Error(errorJson?.message || `HTTP error ${response.status}: ${response.statusText}`);
    }

    // Return JSON output
    return await response.json() as T;
  } catch (error: any) {
    console.warn(`[API Client Warning] Failed to fetch endpoint: ${endpoint}. Details:`, error.message);
    throw error;
  }
}

// --- Centralized API Services ---
export const api = {
  /**
   * AUTHENTICATION SERVICES
   */
  auth: {
    // Authenticate a user
    login: async (role: Role, usernameOrEmail: string, password?: string): Promise<{ token: string; user: any }> => {
      return request<{ token: string; user: any }>('auth/login.php', {
        method: 'POST',
        body: JSON.stringify({ role, username: usernameOrEmail, password }),
      });
    },

    // Register a new parent
    register: async (name: string, email: string, password?: string): Promise<Parent> => {
      return request<Parent>('auth/register.php', {
        method: 'POST',
        body: JSON.stringify({ name, email, password }),
      });
    }
  },

  /**
   * PARENT SERVICES
   */
  parents: {
    // Fetch all parent accounts (Admin master data)
    getAll: async (): Promise<Parent[]> => {
      return request<Parent[]>('parents/list.php');
    },

    // Fetch child profiles belonging to a parent
    getChildren: async (parentId: string): Promise<Child[]> => {
      return request<Child[]>(`children/list.php?parentId=${encodeURIComponent(parentId)}`);
    },

    // Add a new child profile to a parent
    addChild: async (parentId: string, child: Omit<Child, 'id'>): Promise<Child> => {
      return request<Child>('children/create.php', {
        method: 'POST',
        body: JSON.stringify({ parentId, ...child }),
      });
    }
  },

  /**
   * MASTER DATA SERVICES (ADMIN)
   */
  doctors: {
    // Fetch all doctor profiles
    getAll: async (): Promise<Doctor[]> => {
      return request<Doctor[]>('doctors/list.php');
    },

    // Create a new doctor profile
    create: async (doctor: Omit<Doctor, 'id'>): Promise<Doctor> => {
      return request<Doctor>('doctors/create.php', {
        method: 'POST',
        body: JSON.stringify(doctor),
      });
    },

    // Delete a doctor profile
    delete: async (id: string): Promise<{ success: boolean }> => {
      return request<{ success: boolean }>(`doctors/delete.php?id=${encodeURIComponent(id)}`, {
        method: 'DELETE',
      });
    }
  },

  services: {
    // Fetch all available services
    getAll: async (): Promise<Service[]> => {
      return request<Service[]>('services/list.php');
    },

    // Create a new service
    create: async (service: Omit<Service, 'id'>): Promise<Service> => {
      return request<Service>('services/create.php', {
        method: 'POST',
        body: JSON.stringify(service),
      });
    },

    // Delete a service
    delete: async (id: string): Promise<{ success: boolean }> => {
      return request<{ success: boolean }>(`services/delete.php?id=${encodeURIComponent(id)}`, {
        method: 'DELETE',
      });
    }
  },

  /**
   * BOOKING SERVICES
   */
  bookings: {
    // Fetch all bookings (Admin review panel / parent history)
    getAll: async (parentId?: string): Promise<Booking[]> => {
      const query = parentId ? `?parentId=${encodeURIComponent(parentId)}` : '';
      return request<Booking[]>(`bookings/list.php${query}`);
    },

    // Create a new booking (Parent)
    create: async (booking: Omit<Booking, 'id' | 'status'>): Promise<Booking> => {
      return request<Booking>('bookings/create.php', {
        method: 'POST',
        body: JSON.stringify(booking),
      });
    },

    // Approve or reject a booking (Admin)
    updateStatus: async (id: string, status: 'APPROVED' | 'REJECTED'): Promise<Booking> => {
      return request<Booking>('bookings/update_status.php', {
        method: 'POST',
        body: JSON.stringify({ id, status }),
      });
    }
  },

  /**
   * CLINIC LIVE QUEUE SERVICES
   */
  queue: {
    // Fetch the live queue
    getLiveQueue: async (): Promise<QueueItem[]> => {
      return request<QueueItem[]>('queue/list.php');
    },

    // Update queue status (Doctor: 'Examining' / 'Completed' or Admin)
    updateStatus: async (id: string, status: QueueItem['status']): Promise<QueueItem> => {
      return request<QueueItem>('queue/update_status.php', {
        method: 'POST',
        body: JSON.stringify({ id, status }),
      });
    }
  },

  /**
   * MEDICAL RECORDS (REKAM MEDIS)
   */
  medicalRecords: {
    // Fetch records (optionally filter by parentId to show children's history)
    getAll: async (parentId?: string): Promise<MedicalRecord[]> => {
      const query = parentId ? `?parentId=${encodeURIComponent(parentId)}` : '';
      return request<MedicalRecord[]>(`medical_records/list.php${query}`);
    },

    // Add a new medical record diagnosis + prescription (Doctor)
    create: async (record: Omit<MedicalRecord, 'id'>): Promise<MedicalRecord> => {
      return request<MedicalRecord>('medical_records/create.php', {
        method: 'POST',
        body: JSON.stringify(record),
      });
    }
  },

  /**
   * INVOICE & FINANCIAL SERVICES
   */
  invoices: {
    // Fetch invoices (optionally filter by parentId)
    getAll: async (parentId?: string): Promise<Invoice[]> => {
      const query = parentId ? `?parentId=${encodeURIComponent(parentId)}` : '';
      return request<Invoice[]>(`invoices/list.php${query}`);
    },

    // Pay invoice (Parent: updates status to PENDING_CONFIRMATION)
    pay: async (id: string): Promise<Invoice> => {
      return request<Invoice>('invoices/pay.php', {
        method: 'POST',
        body: JSON.stringify({ id }),
      });
    },

    // Confirm payment (Admin: updates status to PAID)
    confirmPayment: async (id: string): Promise<Invoice> => {
      return request<Invoice>('invoices/confirm.php', {
        method: 'POST',
        body: JSON.stringify({ id }),
      });
    }
  }
};
