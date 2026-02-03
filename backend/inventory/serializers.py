from django.contrib.auth.models import Group
from rest_framework import serializers
from .models import Vehicle, Package, ServiceRecord, VehicleImage, VehicleComment


class PackageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Package
        fields = ["id", "name", "description"]


class ServiceRecordSerializer(serializers.ModelSerializer):
    class Meta:
        model = ServiceRecord
        fields = [
            "id",
            "vehicle",
            "service_date",
            "vendor",
            "odometer",
            "category",
            "description",
            "cost",
            "invoice_url",
            "created_at",
            "created_by",
        ]
        read_only_fields = ["created_at", "created_by"]


class VehicleImageSerializer(serializers.ModelSerializer):
    class Meta:
        model = VehicleImage
        fields = ["id", "vehicle", "image_url", "is_primary", "created_at"]
        read_only_fields = ["created_at"]


class VehicleCommentSerializer(serializers.ModelSerializer):
    class Meta:
        model = VehicleComment
        fields = ["id", "vehicle", "comment", "created_by", "created_at"]
        read_only_fields = ["created_at", "created_by"]


class VehicleSerializer(serializers.ModelSerializer):
    packages = serializers.PrimaryKeyRelatedField(many=True, queryset=Package.objects.all(), required=False)
    profit_estimate = serializers.ReadOnlyField()
    service_cost_sum = serializers.ReadOnlyField()

    class Meta:
        model = Vehicle
        fields = [
            "id",
            "vin",
            "manufacture",
            "model",
            "year",
            "color",
            "trim",
            "body_type",
            "drivetrain",
            "engine",
            "transmission",
            "fuel_type",
            "mileage",
            "condition_grade",
            "title_status",
            "carfax_url",
            "status",
            "location",
            "purchase_price",
            "list_price",
            "market_price",
            "profit_estimate",
            "service_cost_sum",
            "packages",
            "notes",
            "acquisition_channel",
            "purchase_date",
            "expected_ready_date",
            "reservation",
            "documents",
            "status_history",
            "created_at",
            "updated_at",
            "created_by",
            "updated_by",
        ]
        read_only_fields = ["created_at", "updated_at", "created_by", "updated_by"]


class VehicleDetailSerializer(VehicleSerializer):
    packages = PackageSerializer(many=True, read_only=True)
    service_records = ServiceRecordSerializer(many=True, read_only=True)
    images = VehicleImageSerializer(many=True, read_only=True)
    comments = VehicleCommentSerializer(many=True, read_only=True)

    class Meta(VehicleSerializer.Meta):
        fields = VehicleSerializer.Meta.fields + ["service_records", "images", "comments"]


class UserMeSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    username = serializers.CharField()
    email = serializers.EmailField(allow_blank=True)
    role = serializers.CharField()


class GroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = Group
        fields = ["id", "name"]
