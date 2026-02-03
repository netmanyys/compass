from django.core.management.base import BaseCommand
from django.contrib.auth.models import Group, User
from django.utils import timezone
from inventory.models import Vehicle, Package, ServiceRecord, VehicleComment, VehicleImage


class Command(BaseCommand):
    help = "Seed demo data: groups, packages, vehicles, service records."

    def handle(self, *args, **options):
        admin_group, _ = Group.objects.get_or_create(name="admin")
        staff_group, _ = Group.objects.get_or_create(name="staff")

        if not User.objects.filter(username="admin").exists():
            admin_user = User.objects.create_user("admin", password="admin123")
            admin_user.groups.add(admin_group)
            self.stdout.write(self.style.SUCCESS("Created admin user admin/admin123"))

        if not User.objects.filter(username="staff").exists():
            staff_user = User.objects.create_user("staff", password="staff123")
            staff_user.groups.add(staff_group)
            self.stdout.write(self.style.SUCCESS("Created staff user staff/staff123"))

        pkg_names = ["Premium Audio", "Sport Package", "Winter Package"]
        packages = []
        for name in pkg_names:
            pkg, _ = Package.objects.get_or_create(name=name)
            packages.append(pkg)

        if Vehicle.objects.exists():
            self.stdout.write(self.style.WARNING("Vehicles already exist. Skipping vehicle seeding."))
            return

        vehicles = [
            Vehicle(
                vin="1HGCM82633A123456",
                manufacture="Honda",
                model="Accord",
                year=2019,
                color="Black",
                trim="EX",
                body_type="Sedan",
                drivetrain="FWD",
                engine="2.0L",
                transmission="Automatic",
                fuel_type="Gas",
                mileage=45000,
                condition_grade="B",
                title_status=Vehicle.TitleStatus.CLEAN,
                status=Vehicle.Status.IN_STOCK,
                location="Lot A",
                purchase_price=15000,
                list_price=18500,
                market_price=19000,
                acquisition_channel="Auction",
                purchase_date=timezone.now().date(),
            ),
            Vehicle(
                vin="2C3CDXBG5MH123456",
                manufacture="Dodge",
                model="Charger",
                year=2020,
                color="White",
                trim="GT",
                body_type="Sedan",
                drivetrain="RWD",
                engine="3.6L",
                transmission="Automatic",
                fuel_type="Gas",
                mileage=30000,
                condition_grade="A",
                title_status=Vehicle.TitleStatus.CLEAN,
                status=Vehicle.Status.IN_SERVICE,
                location="Service Bay",
                purchase_price=22000,
                list_price=26000,
                market_price=25500,
                acquisition_channel="Trade-In",
                purchase_date=timezone.now().date(),
                expected_ready_date=timezone.now().date(),
            ),
        ]

        for vehicle in vehicles:
            vehicle.save()
            vehicle.packages.add(packages[0])

            ServiceRecord.objects.create(
                vehicle=vehicle,
                service_date=timezone.now().date(),
                vendor="QuickFix Auto",
                odometer=vehicle.mileage,
                category=ServiceRecord.Category.MAINTENANCE,
                description="Oil change and inspection",
                cost=120,
            )
            VehicleComment.objects.create(vehicle=vehicle, comment="Initial intake completed.")
            VehicleImage.objects.create(vehicle=vehicle, image_url="https://placehold.co/600x400", is_primary=True)

        self.stdout.write(self.style.SUCCESS("Seed data created."))
