package com.example.klinikanakbarulagi.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.klinikanakbarulagi.model.*
import java.util.UUID

class ClinicViewModel : ViewModel() {

    // --- Active Session ---
    var role by mutableStateOf<Role?>(null)
        private set
    var currentParentId by mutableStateOf("parent-1")
        private set
    var currentDoctorId by mutableStateOf("doc-1")
        private set
    var currentAdminId by mutableStateOf("admin-1")
        private set

    // --- Clinic Database ---
    val parents = mutableStateListOf<Parent>(
        Parent("parent-1", "Bunda Sarah", "sarah@ceria.com", "Premium Member"),
        Parent("parent-2", "Bpk. Ridwan", "ridwan@ceria.com", "Basic Member")
    )

    val admins = listOf(
        Parent("admin-1", "Admin CeriaCare", "admin1@ceria.com", "Staff"),
        Parent("admin-2", "Admin Wiratuwa", "admin2@ceria.com", "Staff")
    )

    val childrenDb = mutableMapOf<String, MutableList<Child>>(
        "parent-1" to mutableStateListOf(
            Child("child-1", "Arka Pratama", "5 tahun", "Laki-laki"),
            Child("child-2", "Ziva Putri", "2 tahun", "Perempuan")
        ),
        "parent-2" to mutableStateListOf(
            Child("child-3", "Siska Amelia", "3 tahun", "Perempuan")
        )
    )

    val doctors = mutableStateListOf<Doctor>(
        Doctor("doc-1", "dr. Sarah Wijaya, Sp.A", "Poli Tumbuh Kembang"),
        Doctor("doc-2", "dr. Budi Santoso, Sp.A", "Poli Anak Umum"),
        Doctor("doc-3", "dr. Rian Pratama, Sp.A", "Spesialis Gizi Anak")
    )

    val services = mutableStateListOf<Service>(
        Service("srv-1", "Konsultasi Umum", 150000.0),
        Service("srv-2", "Tumbuh Kembang", 250000.0),
        Service("srv-3", "Vaksinasi DPT", 450000.0)
    )

    val bookings = mutableStateListOf<Booking>(
        Booking(
            id = "book-1",
            parentId = "parent-1",
            parentName = "Bunda Sarah",
            childName = "Arka Pratama",
            serviceName = "Tumbuh Kembang",
            doctorName = "dr. Sarah Wijaya, Sp.A",
            date = "2026-05-22",
            time = "10:15",
            status = BookingStatus.APPROVED
        ),
        Booking(
            id = "book-2",
            parentId = "parent-2",
            parentName = "Bpk. Ridwan",
            childName = "Siska Amelia",
            serviceName = "Konsultasi Umum",
            doctorName = "dr. Budi Santoso, Sp.A",
            date = "2026-05-22",
            time = "09:45",
            status = BookingStatus.APPROVED
        )
    )

    val queue = mutableStateListOf<QueueItem>(
        QueueItem(
            id = "q-1",
            no = "A-12",
            childName = "Arka Pratama",
            doctorName = "dr. Sarah Wijaya, Sp.A",
            time = "10:15",
            status = QueueStatus.Examining,
            patientId = "child-1",
            parentId = "parent-1"
        ),
        QueueItem(
            id = "q-2",
            no = "A-13",
            childName = "Siska Amelia",
            doctorName = "dr. Budi Santoso, Sp.A",
            time = "09:45",
            status = QueueStatus.Waiting,
            patientId = "child-3",
            parentId = "parent-2"
        )
    )

    val medicalRecords = mutableStateListOf<MedicalRecord>(
        MedicalRecord(
            id = "mr-1",
            childName = "Arka Pratama",
            parentId = "parent-1",
            doctorName = "dr. Sarah Wijaya, Sp.A",
            serviceName = "Poli Tumbuh Kembang",
            date = "10 April 2026",
            height = "110 cm",
            weight = "18 kg",
            notes = "Tumbuh kembang sangat baik, tinggi bertambah 2cm. Disarankan suplemen vitamin D3 harian.",
            prescription = listOf(
                PrescriptionItem("Vitamin D3 Drops", "1x1 drop sehari setelah makan"),
                PrescriptionItem("Sangobion Kids Syrup", "1x1 sendok teh")
            )
        )
    )

    val invoices = mutableStateListOf<Invoice>(
        Invoice(
            id = "INV-009",
            parentId = "parent-1",
            childName = "Arka Pratama",
            item = "Pemeriksaan Tumbuh Kembang - Arka",
            price = 250000.0,
            status = InvoiceStatus.PENDING
        ),
        Invoice(
            id = "INV-008",
            parentId = "parent-1",
            childName = "Ziva Putri",
            item = "Vaksin DPT + Konsultasi - Ziva",
            price = 450000.0,
            status = InvoiceStatus.PAID
        )
    )

    // --- Session Handlers ---
    fun loginAsParent(parentId: String) {
        currentParentId = parentId
        role = Role.PARENT
    }

    fun loginAsDoctor(doctorId: String) {
        currentDoctorId = doctorId
        role = Role.DOCTOR
    }

    fun loginAsAdmin(adminId: String) {
        currentAdminId = adminId
        role = Role.ADMIN
    }

    fun logout() {
        role = null
    }

    // --- Parent Actions ---
    fun registerParent(name: String, email: String) {
        val newId = "parent-${parents.size + 1}"
        val newParent = Parent(newId, name, email, "Basic Member")
        parents.add(newParent)
        childrenDb[newId] = mutableStateListOf()
        currentParentId = newId
        role = Role.PARENT
    }

    fun addChildToParent(parentId: String, name: String, age: String, gender: String) {
        val childList = childrenDb[parentId] ?: mutableStateListOf<Child>().also { childrenDb[parentId] = it }
        val newChildId = "child-${System.currentTimeMillis()}"
        childList.add(Child(newChildId, name, age, gender))
    }

    fun createBooking(
        parentId: String,
        parentName: String,
        childName: String,
        serviceName: String,
        doctorName: String,
        date: String,
        time: String
    ) {
        val newBookingId = "book-${System.currentTimeMillis()}"
        bookings.add(
            Booking(
                id = newBookingId,
                parentId = parentId,
                parentName = parentName,
                childName = childName,
                serviceName = serviceName,
                doctorName = doctorName,
                date = date,
                time = time,
                status = BookingStatus.PENDING
            )
        )
    }

    fun payInvoice(invoiceId: String) {
        val index = invoices.indexOfFirst { it.id == invoiceId }
        if (index != -1) {
            val oldInvoice = invoices[index]
            invoices[index] = oldInvoice.copy(status = InvoiceStatus.PENDING_CONFIRMATION)
        }
    }

    // --- Admin Actions ---
    fun approveBooking(bookingId: String) {
        val index = bookings.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val booking = bookings[index]
            bookings[index] = booking.copy(status = BookingStatus.APPROVED)

            // Find child inside parent's childrenDb to pass along
            val parentChildren = childrenDb[booking.parentId] ?: emptyList()
            val child = parentChildren.find { it.name == booking.childName }
            val patientId = child?.id ?: "child-unknown"

            // Add to queue
            val nextQueueNumber = "A-${10 + queue.size}"
            queue.add(
                QueueItem(
                    id = "q-${System.currentTimeMillis()}",
                    no = nextQueueNumber,
                    childName = booking.childName,
                    doctorName = booking.doctorName,
                    time = booking.time,
                    status = QueueStatus.Waiting,
                    patientId = patientId,
                    parentId = booking.parentId
                )
            )
        }
    }

    fun rejectBooking(bookingId: String) {
        val index = bookings.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            bookings[index] = bookings[index].copy(status = BookingStatus.REJECTED)
        }
    }

    fun updateQueueStatus(queueId: String, newStatus: QueueStatus) {
        val index = queue.indexOfFirst { it.id == queueId }
        if (index != -1) {
            queue[index] = queue[index].copy(status = newStatus)
        }
    }

    fun verifyPayment(invoiceId: String) {
        val index = invoices.indexOfFirst { it.id == invoiceId }
        if (index != -1) {
            invoices[index] = invoices[index].copy(status = InvoiceStatus.PAID)
        }
    }

    fun addService(name: String, price: Double) {
        val newId = "srv-${System.currentTimeMillis()}"
        services.add(Service(newId, name, price))
    }

    fun deleteService(serviceId: String) {
        services.removeIf { it.id == serviceId }
    }

    fun addDoctor(name: String, specialty: String) {
        val newId = "doc-${System.currentTimeMillis()}"
        doctors.add(Doctor(newId, name, specialty))
    }

    fun deleteDoctor(doctorId: String) {
        doctors.removeIf { it.id == doctorId }
    }

    // --- Doctor Actions ---
    fun submitMedicalRecord(
        queueId: String,
        childName: String,
        parentId: String,
        doctorName: String,
        serviceName: String,
        notes: String,
        height: String?,
        weight: String?,
        prescription: List<PrescriptionItem>,
        servicePrice: Double
    ) {
        // Create Medical Record
        val recordId = "mr-${System.currentTimeMillis()}"
        val recordDate = "22 Mei 2026"
        medicalRecords.add(
            MedicalRecord(
                id = recordId,
                childName = childName,
                parentId = parentId,
                doctorName = doctorName,
                serviceName = serviceName,
                date = recordDate,
                notes = notes,
                height = height,
                weight = weight,
                prescription = prescription
            )
        )

        // Create Invoice
        val invoiceId = "INV-${100 + invoices.size}"
        invoices.add(
            Invoice(
                id = invoiceId,
                parentId = parentId,
                childName = childName,
                item = "Pemeriksaan $serviceName - $childName",
                price = servicePrice,
                status = InvoiceStatus.PENDING
            )
        )

        // Mark Queue as Completed
        val qIndex = queue.indexOfFirst { it.id == queueId }
        if (qIndex != -1) {
            queue[qIndex] = queue[qIndex].copy(status = QueueStatus.Completed)
        }
    }
}
