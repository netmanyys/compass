from django.conf import settings
from django.core.validators import MinValueValidator, MaxValueValidator
from django.db import models
from django.db.models import Sum


def current_year():
    return 2100


class Package(models.Model):
    name = models.CharField(max_length=120, unique=True)
    description = models.TextField(blank=True)

    def __str__(self):
        return self.name


class Vehicle(models.Model):
    class Status(models.TextChoices):
        IN_STOCK = "IN_STOCK", "In Stock"
        RESERVED = "RESERVED", "Reserved"
        SOLD = "SOLD", "Sold"
        IN_SERVICE = "IN_SERVICE", "In Service"
        IN_TRANSIT = "IN_TRANSIT", "In Transit"

    class TitleStatus(models.TextChoices):
        CLEAN = "CLEAN", "Clean"
        SALVAGE = "SALVAGE", "Salvage"
        LEMON = "LEMON", "Lemon"
        UNKNOWN = "UNKNOWN", "Unknown"

    vin = models.CharField(max_length=17, unique=True, db_index=True)
    manufacture = models.CharField(max_length=120)
    model = models.CharField(max_length=120)
    year = models.IntegerField(validators=[MinValueValidator(1980), MaxValueValidator(current_year())])
    color = models.CharField(max_length=80)
    trim = models.CharField(max_length=120, blank=True)
    body_type = models.CharField(max_length=80)
    drivetrain = models.CharField(max_length=40, blank=True)
    engine = models.CharField(max_length=120, blank=True)
    transmission = models.CharField(max_length=80, blank=True)
    fuel_type = models.CharField(max_length=40, blank=True)
    mileage = models.IntegerField(validators=[MinValueValidator(0)])
    condition_grade = models.CharField(max_length=20)
    title_status = models.CharField(max_length=20, choices=TitleStatus.choices, default=TitleStatus.UNKNOWN)
    carfax_url = models.URLField(blank=True)
    status = models.CharField(max_length=20, choices=Status.choices, default=Status.IN_STOCK, db_index=True)
    location = models.CharField(max_length=120)
    purchase_price = models.DecimalField(max_digits=12, decimal_places=2, validators=[MinValueValidator(0)])
    list_price = models.DecimalField(max_digits=12, decimal_places=2, validators=[MinValueValidator(0)], db_index=True)
    market_price = models.DecimalField(max_digits=12, decimal_places=2, validators=[MinValueValidator(0)], null=True, blank=True)
    packages = models.ManyToManyField(Package, related_name="vehicles", blank=True)
    notes = models.TextField(blank=True)

    acquisition_channel = models.CharField(max_length=60, blank=True)
    purchase_date = models.DateField(null=True, blank=True)
    expected_ready_date = models.DateField(null=True, blank=True)
    reservation = models.JSONField(null=True, blank=True)
    documents = models.JSONField(null=True, blank=True)
    status_history = models.JSONField(null=True, blank=True)

    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="created_vehicles",
    )
    updated_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="updated_vehicles",
    )

    class Meta:
        indexes = [
            models.Index(fields=["manufacture", "model", "year"]),
            models.Index(fields=["status"]),
            models.Index(fields=["list_price"]),
            models.Index(fields=["mileage"]),
        ]

    def __str__(self):
        return f"{self.vin} {self.manufacture} {self.model}"

    @property
    def service_cost_sum(self):
        return self.service_records.aggregate(total=Sum("cost"))["total"] or 0

    @property
    def profit_estimate(self):
        return (self.list_price or 0) - (self.purchase_price or 0) - self.service_cost_sum


class ServiceRecord(models.Model):
    class Category(models.TextChoices):
        MAINTENANCE = "MAINTENANCE", "Maintenance"
        REPAIR = "REPAIR", "Repair"
        DETAILING = "DETAILING", "Detailing"
        TIRES = "TIRES", "Tires"
        OTHER = "OTHER", "Other"

    vehicle = models.ForeignKey(Vehicle, related_name="service_records", on_delete=models.CASCADE)
    service_date = models.DateField()
    vendor = models.CharField(max_length=120)
    odometer = models.IntegerField(null=True, blank=True, validators=[MinValueValidator(0)])
    category = models.CharField(max_length=20, choices=Category.choices)
    description = models.TextField()
    cost = models.DecimalField(max_digits=12, decimal_places=2, validators=[MinValueValidator(0)])
    invoice_url = models.URLField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="created_service_records",
    )

    def __str__(self):
        return f"{self.vehicle.vin} {self.category}"


class VehicleImage(models.Model):
    vehicle = models.ForeignKey(Vehicle, related_name="images", on_delete=models.CASCADE)
    image_url = models.URLField()
    is_primary = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)


class VehicleComment(models.Model):
    vehicle = models.ForeignKey(Vehicle, related_name="comments", on_delete=models.CASCADE)
    comment = models.TextField()
    created_by = models.ForeignKey(
        settings.AUTH_USER_MODEL,
        null=True,
        blank=True,
        on_delete=models.SET_NULL,
        related_name="vehicle_comments",
    )
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Comment {self.pk} on {self.vehicle.vin}"
