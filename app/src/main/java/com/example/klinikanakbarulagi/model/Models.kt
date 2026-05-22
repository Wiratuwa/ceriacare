package com.example.klinikanakbarulagi.model

enum class Role {
    PARENT,
    DOCTOR,
    ADMIN
}

data class Child(
    val id: String,
    val name: String,
    val age: String,
    val gender: String
)

data class Parent(
    val id: String,
    val name: String,
    val email: String,
    val memberType: String
)

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String
)

data class Service(
    val id: String,
    val name: String,
    val price: Double
)

enum class BookingStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class Booking(
    val id: String,
    val parentId: String,
    val parentName: String,
    val childName: String,
    val serviceName: String,
    val doctorName: String,
    val date: String,
    val time: String,
    val status: BookingStatus
)

enum class QueueStatus {
    Waiting,
    Examining,
    Completed
}

data class QueueItem(
    val id: String,
    val no: String,
    val childName: String,
    val doctorName: String,
    val time: String,
    val status: QueueStatus,
    val patientId: String,
    val parentId: String
)

data class PrescriptionItem(
    val name: String,
    val dosage: String
)

data class MedicalRecord(
    val id: String,
    val childName: String,
    val parentId: String,
    val doctorName: String,
    val serviceName: String,
    val date: String,
    val notes: String,
    val height: String? = null,
    val weight: String? = null,
    val prescription: List<PrescriptionItem> = emptyList()
)

enum class InvoiceStatus {
    PENDING,
    PENDING_CONFIRMATION,
    PAID
}

data class Invoice(
    val id: String,
    val parentId: String,
    val childName: String,
    val item: String,
    val price: Double,
    val status: InvoiceStatus
)
