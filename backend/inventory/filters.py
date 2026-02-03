import django_filters
from django.db.models import Q
from .models import Vehicle


class VehicleFilter(django_filters.FilterSet):
    year_min = django_filters.NumberFilter(field_name="year", lookup_expr="gte")
    year_max = django_filters.NumberFilter(field_name="year", lookup_expr="lte")
    price_min = django_filters.NumberFilter(field_name="list_price", lookup_expr="gte")
    price_max = django_filters.NumberFilter(field_name="list_price", lookup_expr="lte")
    mileage_min = django_filters.NumberFilter(field_name="mileage", lookup_expr="gte")
    mileage_max = django_filters.NumberFilter(field_name="mileage", lookup_expr="lte")
    manufacture = django_filters.CharFilter(field_name="manufacture", lookup_expr="icontains")
    model = django_filters.CharFilter(field_name="model", lookup_expr="icontains")
    vin = django_filters.CharFilter(field_name="vin", lookup_expr="iexact")
    packages = django_filters.ModelMultipleChoiceFilter(field_name="packages", to_field_name="id", queryset=None)
    keyword = django_filters.CharFilter(method="filter_keyword")

    class Meta:
        model = Vehicle
        fields = [
            "vin",
            "manufacture",
            "model",
            "year_min",
            "year_max",
            "price_min",
            "price_max",
            "mileage_min",
            "mileage_max",
            "color",
            "status",
            "condition_grade",
            "packages",
        ]

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        package_model = Vehicle._meta.get_field("packages").remote_field.model
        self.filters["packages"].queryset = package_model.objects.all()

    def filter_keyword(self, queryset, name, value):
        return queryset.filter(
            Q(vin__icontains=value)
            | Q(manufacture__icontains=value)
            | Q(model__icontains=value)
            | Q(color__icontains=value)
            | Q(trim__icontains=value)
            | Q(body_type__icontains=value)
            | Q(location__icontains=value)
        )
