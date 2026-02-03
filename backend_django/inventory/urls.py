from django.urls import path, include
from rest_framework.routers import DefaultRouter
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

from .views import (
    VehicleViewSet,
    PackageViewSet,
    ServiceRecordViewSet,
    VehicleImageViewSet,
    VehicleCommentViewSet,
    MeView,
)

router = DefaultRouter()
router.register(r"vehicles", VehicleViewSet, basename="vehicle")
router.register(r"packages", PackageViewSet, basename="package")
router.register(r"service-records", ServiceRecordViewSet, basename="service-record")
router.register(r"images", VehicleImageViewSet, basename="vehicle-image")
router.register(r"comments", VehicleCommentViewSet, basename="vehicle-comment")

urlpatterns = [
    path("auth/login/", TokenObtainPairView.as_view(), name="token_obtain_pair"),
    path("auth/refresh/", TokenRefreshView.as_view(), name="token_refresh"),
    path("auth/me/", MeView.as_view(), name="me"),
    path("", include(router.urls)),
    path(
        "vehicles/<int:vehicle_id>/service-records/",
        ServiceRecordViewSet.as_view({"get": "list", "post": "create"}),
        name="vehicle-service-records",
    ),
    path(
        "vehicles/<int:vehicle_id>/service-records/<int:pk>/",
        ServiceRecordViewSet.as_view({"get": "retrieve", "patch": "partial_update", "delete": "destroy"}),
        name="vehicle-service-record-detail",
    ),
    path(
        "vehicles/<int:vehicle_id>/images/",
        VehicleImageViewSet.as_view({"get": "list", "post": "create"}),
        name="vehicle-images",
    ),
    path(
        "vehicles/<int:vehicle_id>/images/<int:pk>/",
        VehicleImageViewSet.as_view({"get": "retrieve", "patch": "partial_update", "delete": "destroy"}),
        name="vehicle-image-detail",
    ),
    path(
        "vehicles/<int:vehicle_id>/comments/",
        VehicleCommentViewSet.as_view({"get": "list", "post": "create"}),
        name="vehicle-comments",
    ),
    path(
        "vehicles/<int:vehicle_id>/comments/<int:pk>/",
        VehicleCommentViewSet.as_view({"get": "retrieve", "patch": "partial_update", "delete": "destroy"}),
        name="vehicle-comment-detail",
    ),
]
