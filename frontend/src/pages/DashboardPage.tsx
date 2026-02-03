import React, { useEffect, useState } from "react";
import api from "../api/client";
import Filters, { FiltersState } from "../components/Filters";
import VehicleTable from "../components/VehicleTable";
import { Package, Vehicle } from "../types/inventory";

const defaultFilters: FiltersState = {
  vin: "",
  manufacture: "",
  model: "",
  year_min: "",
  year_max: "",
  price_min: "",
  price_max: "",
  mileage_min: "",
  mileage_max: "",
  color: "",
  status: "",
  condition_grade: "",
  packages: [],
  keyword: "",
};

const DashboardPage: React.FC = () => {
  const [filters, setFilters] = useState<FiltersState>(defaultFilters);
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [packages, setPackages] = useState<Package[]>([]);
  const [loading, setLoading] = useState(false);

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
      const response = await api.get("/vehicles/", { params: buildParams() });
      setVehicles(response.data.results || []);
    } finally {
      setLoading(false);
    }
  };

  const fetchPackages = async () => {
    const response = await api.get("/packages/");
    setPackages(response.data.results || response.data);
  };

  useEffect(() => {
    fetchPackages();
    fetchVehicles();
  }, []);

  return (
    <div className="container">
      <div className="hero">
        <div>
          <h2>Inventory Dashboard</h2>
          <p>Search, filter, and track every vehicle in real time.</p>
        </div>
      </div>
      <div className="stat-row">
        <div className="stat">
          <span>Vehicles Loaded</span>
          <strong>{vehicles.length}</strong>
        </div>
        <div className="stat">
          <span>Active Filters</span>
          <strong>{Object.values(filters).filter((value) => (Array.isArray(value) ? value.length : value.trim?.())).length}</strong>
        </div>
        <div className="stat">
          <span>Status View</span>
          <strong>{filters.status || "All"}</strong>
        </div>
      </div>
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
      <div className="card" style={{ marginTop: 16 }}>
        {loading ? <p>Loading...</p> : <VehicleTable vehicles={vehicles} />}
      </div>
    </div>
  );
};

export default DashboardPage;
