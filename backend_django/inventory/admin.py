from django.contrib import admin
from .models import Vehicle, Package, ServiceRecord, VehicleImage, VehicleComment


@admin.register(Vehicle)
class VehicleAdmin(admin.ModelAdmin):
    list_display = ("vin", "manufacture", "model", "year", "status", "list_price")
    search_fields = ("vin", "manufacture", "model")
    list_filter = ("status", "manufacture", "year")


admin.site.register(Package)
admin.site.register(ServiceRecord)
admin.site.register(VehicleImage)
admin.site.register(VehicleComment)
