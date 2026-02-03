from django.contrib.auth.models import Group, User
from rest_framework.test import APITestCase
from rest_framework import status
from inventory.models import Vehicle, ServiceRecord


class RBACVehicleTests(APITestCase):
    def setUp(self):
        self.admin_group, _ = Group.objects.get_or_create(name="admin")
        self.staff_group, _ = Group.objects.get_or_create(name="staff")

        self.admin = User.objects.create_user(username="admin_test", password="pass1234")
        self.admin.groups.add(self.admin_group)

        self.staff = User.objects.create_user(username="staff_test", password="pass1234")
        self.staff.groups.add(self.staff_group)

        self.vehicle = Vehicle.objects.create(
            vin="3FA6P0H74HR123456",
            manufacture="Ford",
            model="Fusion",
            year=2018,
            color="Blue",
            body_type="Sedan",
            mileage=60000,
            condition_grade="B",
            title_status=Vehicle.TitleStatus.CLEAN,
            status=Vehicle.Status.IN_STOCK,
            location="Lot B",
            purchase_price=9000,
            list_price=12000,
        )

    def _login(self, username, password):
        response = self.client.post("/api/auth/login/", {"username": username, "password": password}, format="json")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        return response.data["access"]

    def _login_pair(self, username, password):
        response = self.client.post("/api/auth/login/", {"username": username, "password": password}, format="json")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        return response.data["access"], response.data["refresh"]

    def test_staff_cannot_create_vehicle(self):
        token = self._login("staff_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.post(
            "/api/vehicles/",
            {
                "vin": "1C4RJFBG5FC123456",
                "manufacture": "Jeep",
                "model": "Grand Cherokee",
                "year": 2017,
                "color": "Gray",
                "body_type": "SUV",
                "mileage": 70000,
                "condition_grade": "C",
                "title_status": Vehicle.TitleStatus.CLEAN,
                "status": Vehicle.Status.IN_STOCK,
                "location": "Lot C",
                "purchase_price": 10000,
                "list_price": 14000,
            },
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)

    def test_admin_can_create_vehicle(self):
        token = self._login("admin_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.post(
            "/api/vehicles/",
            {
                "vin": "1C4RJFBG5FC654321",
                "manufacture": "Jeep",
                "model": "Grand Cherokee",
                "year": 2017,
                "color": "Gray",
                "body_type": "SUV",
                "mileage": 70000,
                "condition_grade": "C",
                "title_status": Vehicle.TitleStatus.CLEAN,
                "status": Vehicle.Status.IN_STOCK,
                "location": "Lot C",
                "purchase_price": 10000,
                "list_price": 14000,
            },
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

    def test_vehicle_list_access(self):
        token = self._login("staff_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.get("/api/vehicles/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue(response.data["results"])

    def test_me_endpoint_returns_role(self):
        token = self._login("admin_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.get("/api/auth/me/")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data["role"], "admin")

    def test_filter_by_vin(self):
        token = self._login("staff_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.get("/api/vehicles/", {"vin": "3FA6P0H74HR123456"})
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data["results"]), 1)

    def test_price_range_filter(self):
        token = self._login("staff_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.get("/api/vehicles/", {"price_min": 11000, "price_max": 13000})
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data["results"]), 1)

    def test_staff_cannot_create_service_record(self):
        token = self._login("staff_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.post(
            f"/api/vehicles/{self.vehicle.id}/service-records/",
            {
                "vehicle": self.vehicle.id,
                "service_date": "2024-01-01",
                "vendor": "Test Shop",
                "category": ServiceRecord.Category.REPAIR,
                "description": "Test",
                "cost": 100,
            },
            format="json",
        )
        self.assertEqual(response.status_code, status.HTTP_403_FORBIDDEN)

    def test_jwt_refresh(self):
        _, refresh = self._login_pair("staff_test", "pass1234")
        response = self.client.post("/api/auth/refresh/", {"refresh": refresh}, format="json")
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertIn("access", response.data)

    def test_ordering_by_year_desc(self):
        Vehicle.objects.create(
            vin="5YJ3E1EA7KF123456",
            manufacture="Tesla",
            model="Model 3",
            year=2021,
            color="White",
            body_type="Sedan",
            mileage=12000,
            condition_grade="A",
            title_status=Vehicle.TitleStatus.CLEAN,
            status=Vehicle.Status.IN_STOCK,
            location="Lot C",
            purchase_price=28000,
            list_price=32000,
        )
        Vehicle.objects.create(
            vin="1N4AL3AP9JC123456",
            manufacture="Nissan",
            model="Altima",
            year=2015,
            color="Silver",
            body_type="Sedan",
            mileage=80000,
            condition_grade="C",
            title_status=Vehicle.TitleStatus.CLEAN,
            status=Vehicle.Status.IN_STOCK,
            location="Lot D",
            purchase_price=6000,
            list_price=9000,
        )

        token = self._login("staff_test", "pass1234")
        self.client.credentials(HTTP_AUTHORIZATION=f"Bearer {token}")
        response = self.client.get("/api/vehicles/", {"ordering": "-year"})
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        years = [item["year"] for item in response.data["results"]]
        self.assertEqual(years, sorted(years, reverse=True))
