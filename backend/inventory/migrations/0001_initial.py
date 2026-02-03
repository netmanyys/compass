from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion
import django.core.validators


def current_year():
    return 2100


class Migration(migrations.Migration):
    initial = True

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name="Package",
            fields=[
                ("id", models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name="ID")),
                ("name", models.CharField(max_length=120, unique=True)),
                ("description", models.TextField(blank=True)),
            ],
        ),
        migrations.CreateModel(
            name="Vehicle",
            fields=[
                ("id", models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name="ID")),
                ("vin", models.CharField(db_index=True, max_length=17, unique=True)),
                ("manufacture", models.CharField(max_length=120)),
                ("model", models.CharField(max_length=120)),
                (
                    "year",
                    models.IntegerField(
                        validators=[
                            django.core.validators.MinValueValidator(1980),
                            django.core.validators.MaxValueValidator(current_year()),
                        ]
                    ),
                ),
                ("color", models.CharField(max_length=80)),
                ("trim", models.CharField(blank=True, max_length=120)),
                ("body_type", models.CharField(max_length=80)),
                ("drivetrain", models.CharField(blank=True, max_length=40)),
                ("engine", models.CharField(blank=True, max_length=120)),
                ("transmission", models.CharField(blank=True, max_length=80)),
                ("fuel_type", models.CharField(blank=True, max_length=40)),
                ("mileage", models.IntegerField(validators=[django.core.validators.MinValueValidator(0)])),
                ("condition_grade", models.CharField(max_length=20)),
                (
                    "title_status",
                    models.CharField(
                        choices=[
                            ("CLEAN", "Clean"),
                            ("SALVAGE", "Salvage"),
                            ("LEMON", "Lemon"),
                            ("UNKNOWN", "Unknown"),
                        ],
                        default="UNKNOWN",
                        max_length=20,
                    ),
                ),
                ("carfax_url", models.URLField(blank=True)),
                (
                    "status",
                    models.CharField(
                        choices=[
                            ("IN_STOCK", "In Stock"),
                            ("RESERVED", "Reserved"),
                            ("SOLD", "Sold"),
                            ("IN_SERVICE", "In Service"),
                            ("IN_TRANSIT", "In Transit"),
                        ],
                        db_index=True,
                        default="IN_STOCK",
                        max_length=20,
                    ),
                ),
                ("location", models.CharField(max_length=120)),
                (
                    "purchase_price",
                    models.DecimalField(decimal_places=2, max_digits=12, validators=[django.core.validators.MinValueValidator(0)]),
                ),
                (
                    "list_price",
                    models.DecimalField(
                        db_index=True,
                        decimal_places=2,
                        max_digits=12,
                        validators=[django.core.validators.MinValueValidator(0)],
                    ),
                ),
                (
                    "market_price",
                    models.DecimalField(
                        blank=True,
                        decimal_places=2,
                        max_digits=12,
                        null=True,
                        validators=[django.core.validators.MinValueValidator(0)],
                    ),
                ),
                ("notes", models.TextField(blank=True)),
                ("acquisition_channel", models.CharField(blank=True, max_length=60)),
                ("purchase_date", models.DateField(blank=True, null=True)),
                ("expected_ready_date", models.DateField(blank=True, null=True)),
                ("reservation", models.JSONField(blank=True, null=True)),
                ("documents", models.JSONField(blank=True, null=True)),
                ("status_history", models.JSONField(blank=True, null=True)),
                ("created_at", models.DateTimeField(auto_now_add=True)),
                ("updated_at", models.DateTimeField(auto_now=True)),
                (
                    "created_by",
                    models.ForeignKey(
                        blank=True,
                        null=True,
                        on_delete=django.db.models.deletion.SET_NULL,
                        related_name="created_vehicles",
                        to=settings.AUTH_USER_MODEL,
                    ),
                ),
                (
                    "updated_by",
                    models.ForeignKey(
                        blank=True,
                        null=True,
                        on_delete=django.db.models.deletion.SET_NULL,
                        related_name="updated_vehicles",
                        to=settings.AUTH_USER_MODEL,
                    ),
                ),
                (
                    "packages",
                    models.ManyToManyField(blank=True, related_name="vehicles", to="inventory.package"),
                ),
            ],
            options={
                "indexes": [
                    models.Index(fields=["manufacture", "model", "year"], name="inventory_v_manufac_77e025_idx"),
                    models.Index(fields=["status"], name="inventory_v_status_28f888_idx"),
                    models.Index(fields=["list_price"], name="inventory_v_list_pr_caf39c_idx"),
                    models.Index(fields=["mileage"], name="inventory_v_mileage_850f36_idx"),
                ],
            },
        ),
        migrations.CreateModel(
            name="VehicleImage",
            fields=[
                ("id", models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name="ID")),
                ("image_url", models.URLField()),
                ("is_primary", models.BooleanField(default=False)),
                ("created_at", models.DateTimeField(auto_now_add=True)),
                (
                    "vehicle",
                    models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name="images", to="inventory.vehicle"),
                ),
            ],
        ),
        migrations.CreateModel(
            name="ServiceRecord",
            fields=[
                ("id", models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name="ID")),
                ("service_date", models.DateField()),
                ("vendor", models.CharField(max_length=120)),
                ("odometer", models.IntegerField(blank=True, null=True, validators=[django.core.validators.MinValueValidator(0)])),
                (
                    "category",
                    models.CharField(
                        choices=[
                            ("MAINTENANCE", "Maintenance"),
                            ("REPAIR", "Repair"),
                            ("DETAILING", "Detailing"),
                            ("TIRES", "Tires"),
                            ("OTHER", "Other"),
                        ],
                        max_length=20,
                    ),
                ),
                ("description", models.TextField()),
                (
                    "cost",
                    models.DecimalField(decimal_places=2, max_digits=12, validators=[django.core.validators.MinValueValidator(0)]),
                ),
                ("invoice_url", models.URLField(blank=True)),
                ("created_at", models.DateTimeField(auto_now_add=True)),
                (
                    "created_by",
                    models.ForeignKey(
                        blank=True,
                        null=True,
                        on_delete=django.db.models.deletion.SET_NULL,
                        related_name="created_service_records",
                        to=settings.AUTH_USER_MODEL,
                    ),
                ),
                (
                    "vehicle",
                    models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name="service_records", to="inventory.vehicle"),
                ),
            ],
        ),
        migrations.CreateModel(
            name="VehicleComment",
            fields=[
                ("id", models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name="ID")),
                ("comment", models.TextField()),
                ("created_at", models.DateTimeField(auto_now_add=True)),
                (
                    "created_by",
                    models.ForeignKey(
                        blank=True,
                        null=True,
                        on_delete=django.db.models.deletion.SET_NULL,
                        related_name="vehicle_comments",
                        to=settings.AUTH_USER_MODEL,
                    ),
                ),
                (
                    "vehicle",
                    models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name="comments", to="inventory.vehicle"),
                ),
            ],
        ),
    ]
