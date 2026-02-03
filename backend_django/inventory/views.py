from django.db.models import Prefetch
from rest_framework import viewsets, permissions
from rest_framework.response import Response
from rest_framework.views import APIView

from .filters import VehicleFilter
from .models import Vehicle, Package, ServiceRecord, VehicleImage, VehicleComment
from .permissions import IsAdminOrReadOnly
from .serializers import (
    VehicleSerializer,
    VehicleDetailSerializer,
    PackageSerializer,
    ServiceRecordSerializer,
    VehicleImageSerializer,
    VehicleCommentSerializer,
    UserMeSerializer,
)


def get_user_role(user):
    if user.groups.filter(name="admin").exists():
        return "admin"
    return "staff"


class MeView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        data = {
            "id": request.user.id,
            "username": request.user.username,
            "email": request.user.email,
            "role": get_user_role(request.user),
        }
        return Response(UserMeSerializer(data).data)


class VehicleViewSet(viewsets.ModelViewSet):
    queryset = Vehicle.objects.all().prefetch_related(
        "packages",
        Prefetch("service_records"),
        Prefetch("images"),
        Prefetch("comments"),
    )
    serializer_class = VehicleSerializer
    permission_classes = [IsAdminOrReadOnly]
    filterset_class = VehicleFilter
    search_fields = ["vin", "manufacture", "model", "color", "trim", "body_type", "location"]
    ordering_fields = ["year", "list_price", "mileage", "created_at"]

    def get_serializer_class(self):
        if self.action in ["retrieve"]:
            return VehicleDetailSerializer
        return VehicleSerializer

    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user, updated_by=self.request.user)

    def perform_update(self, serializer):
        serializer.save(updated_by=self.request.user)


class PackageViewSet(viewsets.ModelViewSet):
    queryset = Package.objects.all()
    serializer_class = PackageSerializer
    permission_classes = [IsAdminOrReadOnly]
    ordering_fields = ["name"]


class ServiceRecordViewSet(viewsets.ModelViewSet):
    queryset = ServiceRecord.objects.all()
    serializer_class = ServiceRecordSerializer
    permission_classes = [IsAdminOrReadOnly]
    ordering_fields = ["service_date", "cost", "created_at"]

    def get_queryset(self):
        queryset = super().get_queryset()
        vehicle_id = self.kwargs.get("vehicle_id")
        if vehicle_id:
            return queryset.filter(vehicle_id=vehicle_id)
        return queryset

    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)


class VehicleImageViewSet(viewsets.ModelViewSet):
    queryset = VehicleImage.objects.all()
    serializer_class = VehicleImageSerializer
    permission_classes = [IsAdminOrReadOnly]

    def get_queryset(self):
        queryset = super().get_queryset()
        vehicle_id = self.kwargs.get("vehicle_id")
        if vehicle_id:
            return queryset.filter(vehicle_id=vehicle_id)
        return queryset


class VehicleCommentViewSet(viewsets.ModelViewSet):
    queryset = VehicleComment.objects.all()
    serializer_class = VehicleCommentSerializer
    permission_classes = [IsAdminOrReadOnly]
    ordering_fields = ["created_at"]

    def get_queryset(self):
        queryset = super().get_queryset()
        vehicle_id = self.kwargs.get("vehicle_id")
        if vehicle_id:
            return queryset.filter(vehicle_id=vehicle_id)
        return queryset

    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)
