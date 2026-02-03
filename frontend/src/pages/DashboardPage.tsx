import React, { useEffect, useState } from "react";
import api from "../api/client";
import Filters, { FiltersState } from "../components/Filters";
import VehicleTable from "../components/VehicleTable";
import { Package, Vehicle } from "../types/inventory";
import { useAuth } from "../context/AuthContext";

const defaultFilters: FiltersState = {
  vin: "",
  manufacture: "",
  model: "",
  yearMin: "",
  yearMax: "",
  priceMin: "",
  priceMax: "",
  mileageMin: "",
  mileageMax: "",
  color: "",
  status: "",
  conditionGrade: "",
  packageIds: [],
  keyword: "",
};

const DashboardPage: React.FC = () => {
  const [filters, setFilters] = useState<FiltersState>(defaultFilters);
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [packages, setPackages] = useState<Package[]>([]);
  const [loading, setLoading] = useState(false);
  const [createForm, setCreateForm] = useState({
    vin: "",
    manufacture: "",
    model: "",
    year: "",
    color: "",
    bodyType: "",
    mileage: "",
    conditionGrade: "A",
    titleStatus: "Clean",
    status: "IN_STOCK",
    location: "",
    purchasePrice: "",
    listPrice: "",
  });
  const [createError, setCreateError] = useState("");
  const { user } = useAuth();

  const buildParams = () => {
    const params: Record<string, string> = {};
    Object.entries(filters).forEach(([key, value]) => {
      if (Array.isArray(value) && value.length) {
        params[key] = value.join(",");
      } else if (typeof value === "string" && value.trim()) {
        params[key] = value.trim();
      }
    });
    return params;
  };

  const fetchVehicles = async () => {
    setLoading(true);
    try {
      const response = await api.get("/vehicles", { params: buildParams() });
      setVehicles(response.data.content || []);
    } finally {
      setLoading(false);
    }
  };

  const fetchPackages = async () => {
    const response = await api.get("/packages");
    setPackages(response.data);
  };

  useEffect(() => {
    fetchPackages();
    fetchVehicles();
  }, []);

  const activeChips = [
    filters.manufacture && `Make: ${filters.manufacture}`,
    filters.model && `Model: ${filters.model}`,
    filters.color && `Color: ${filters.color}`,
    filters.status && `Status: ${filters.status}`,
  ].filter(Boolean) as string[];

  return (
    <div className="container">
      <div className="page-layout">
        <aside className="sidebar card">
          <div className="filter-title">Filters</div>
          <Filters
            filters={filters}
            packages={packages}
            onChange={setFilters}
            onSubmit={fetchVehicles}
            onReset={() => {
              setFilters(defaultFilters);
              setTimeout(fetchVehicles, 0);
            }}
          />
        </aside>
        <main>
          <h2>Inventory Dashboard</h2>
          {user?.role === "admin" && (
            <div className="card" style={{ marginTop: 12 }}>
              <h4>Add Vehicle</h4>
              <div className="form-row">
                <input placeholder="VIN" value={createForm.vin} onChange={(e) => setCreateForm({ ...createForm, vin: e.target.value })} />
                <input placeholder="Manufacture" value={createForm.manufacture} onChange={(e) => setCreateForm({ ...createForm, manufacture: e.target.value })} />
                <input placeholder="Model" value={createForm.model} onChange={(e) => setCreateForm({ ...createForm, model: e.target.value })} />
                <input placeholder="Year" value={createForm.year} onChange={(e) => setCreateForm({ ...createForm, year: e.target.value })} />
                <input placeholder="Color" value={createForm.color} onChange={(e) => setCreateForm({ ...createForm, color: e.target.value })} />
                <input placeholder="Body Type" value={createForm.bodyType} onChange={(e) => setCreateForm({ ...createForm, bodyType: e.target.value })} />
              </div>
              <div className="form-row">
                <input placeholder="Mileage" value={createForm.mileage} onChange={(e) => setCreateForm({ ...createForm, mileage: e.target.value })} />
                <input placeholder="Condition Grade" value={createForm.conditionGrade} onChange={(e) => setCreateForm({ ...createForm, conditionGrade: e.target.value })} />
                <input placeholder="Title Status" value={createForm.titleStatus} onChange={(e) => setCreateForm({ ...createForm, titleStatus: e.target.value })} />
                <input placeholder="Status" value={createForm.status} onChange={(e) => setCreateForm({ ...createForm, status: e.target.value })} />
                <input placeholder="Location" value={createForm.location} onChange={(e) => setCreateForm({ ...createForm, location: e.target.value })} />
              </div>
              <div className="form-row">
                <input placeholder="Purchase Price" value={createForm.purchasePrice} onChange={(e) => setCreateForm({ ...createForm, purchasePrice: e.target.value })} />
                <input placeholder="List Price" value={createForm.listPrice} onChange={(e) => setCreateForm({ ...createForm, listPrice: e.target.value })} />
              </div>
              {createError && <div className="form-error">{createError}</div>}
              <button
                className="btn"
                onClick={async () => {
                  setCreateError("");
                  const requiredFields = [
                    createForm.vin,
                    createForm.manufacture,
                    createForm.model,
                    createForm.year,
                    createForm.color,
                    createForm.bodyType,
                    createForm.mileage,
                    createForm.conditionGrade,
                    createForm.titleStatus,
                    createForm.status,
                    createForm.location,
                    createForm.purchasePrice,
                    createForm.listPrice,
                  ];
                  if (requiredFields.some((value) => !String(value).trim())) {
                    setCreateError("Please fill all required fields before creating a vehicle.");
                    return;
                  }
                  try {
                    await api.post("/vehicles", {
                      vin: createForm.vin.trim(),
                      manufacture: createForm.manufacture.trim(),
                      model: createForm.model.trim(),
                      year: Number(createForm.year),
                      color: createForm.color.trim(),
                      bodyType: createForm.bodyType.trim(),
                      mileage: Number(createForm.mileage),
                      conditionGrade: createForm.conditionGrade.trim(),
                      titleStatus: createForm.titleStatus.trim(),
                      status: createForm.status.trim(),
                      location: createForm.location.trim(),
                      purchasePrice: Number(createForm.purchasePrice),
                      listPrice: Number(createForm.listPrice),
                    });
                    setCreateForm({
                      vin: "",
                      manufacture: "",
                      model: "",
                      year: "",
                      color: "",
                      bodyType: "",
                      mileage: "",
                      conditionGrade: "A",
                      titleStatus: "Clean",
                      status: "IN_STOCK",
                      location: "",
                      purchasePrice: "",
                      listPrice: "",
                    });
                    fetchVehicles();
                  } catch (err: any) {
                    const message =
                      err?.response?.data?.message ||
                      err?.response?.data?.error ||
                      "Create failed. Please check the form values.";
                    setCreateError(message);
                  }
                }}
              >
                Create Vehicle
              </button>
            </div>
          )}
          <div className="card search-panel" style={{ marginTop: 12 }}>
            <input
              placeholder="Search make, model, or body style"
              value={filters.keyword}
              onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
            />
            <button className="btn" onClick={fetchVehicles}>Search</button>
          </div>
          {activeChips.length > 0 && (
            <div className="chip-row">
              {activeChips.map((chip) => (
                <span className="chip" key={chip}>{chip}</span>
              ))}
              <button className="btn ghost" onClick={() => {
                setFilters(defaultFilters);
                setTimeout(fetchVehicles, 0);
              }}>
                Clear all
              </button>
            </div>
          )}
          <div className="results-header">
            <div>{vehicles.length} results</div>
            <div>Sort by: Best match</div>
          </div>
          {loading ? <p>Loading...</p> : <VehicleTable vehicles={vehicles} />}
        </main>
      </div>
    </div>
  );
};

export default DashboardPage;
