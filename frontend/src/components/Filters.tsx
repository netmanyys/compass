import React from "react";
import { Package } from "../types/inventory";

type FiltersState = {
  vin: string;
  manufacture: string;
  model: string;
  year_min: string;
  year_max: string;
  price_min: string;
  price_max: string;
  mileage_min: string;
  mileage_max: string;
  color: string;
  status: string;
  condition_grade: string;
  packages: string[];
  keyword: string;
};

type Props = {
  filters: FiltersState;
  packages: Package[];
  onChange: (next: FiltersState) => void;
  onSubmit: () => void;
  onReset: () => void;
};

const Filters: React.FC<Props> = ({ filters, packages, onChange, onSubmit, onReset }) => {
  const update = (key: keyof FiltersState, value: string | string[]) => {
    onChange({ ...filters, [key]: value });
  };

  const togglePackage = (id: string) => {
    const next = filters.packages.includes(id)
      ? filters.packages.filter((pkg) => pkg !== id)
      : [...filters.packages, id];
    update("packages", next);
  };

  return (
    <div className="card">
      <div className="form-row">
        <input
          placeholder="VIN (exact)"
          value={filters.vin}
          onChange={(e) => update("vin", e.target.value)}
        />
        <input
          placeholder="Manufacture"
          value={filters.manufacture}
          onChange={(e) => update("manufacture", e.target.value)}
        />
        <input
          placeholder="Model"
          value={filters.model}
          onChange={(e) => update("model", e.target.value)}
        />
        <input
          placeholder="Color"
          value={filters.color}
          onChange={(e) => update("color", e.target.value)}
        />
        <input
          placeholder="Condition Grade"
          value={filters.condition_grade}
          onChange={(e) => update("condition_grade", e.target.value)}
        />
      </div>

      <div className="form-row">
        <input
          placeholder="Year Min"
          value={filters.year_min}
          onChange={(e) => update("year_min", e.target.value)}
        />
        <input
          placeholder="Year Max"
          value={filters.year_max}
          onChange={(e) => update("year_max", e.target.value)}
        />
        <input
          placeholder="Price Min"
          value={filters.price_min}
          onChange={(e) => update("price_min", e.target.value)}
        />
        <input
          placeholder="Price Max"
          value={filters.price_max}
          onChange={(e) => update("price_max", e.target.value)}
        />
        <input
          placeholder="Mileage Min"
          value={filters.mileage_min}
          onChange={(e) => update("mileage_min", e.target.value)}
        />
        <input
          placeholder="Mileage Max"
          value={filters.mileage_max}
          onChange={(e) => update("mileage_max", e.target.value)}
        />
      </div>

      <div className="form-row">
        <input
          placeholder="Status"
          value={filters.status}
          onChange={(e) => update("status", e.target.value)}
        />
        <input
          placeholder="Keyword"
          value={filters.keyword}
          onChange={(e) => update("keyword", e.target.value)}
        />
      </div>

      <div className="form-row">
        {packages.map((pkg) => (
          <label key={pkg.id}>
            <input
              type="checkbox"
              checked={filters.packages.includes(String(pkg.id))}
              onChange={() => togglePackage(String(pkg.id))}
            />
            {pkg.name}
          </label>
        ))}
      </div>

      <div style={{ display: "flex", gap: 8 }}>
        <button className="btn" onClick={onSubmit}>
          Apply Filters
        </button>
        <button className="btn secondary" onClick={onReset}>
          Reset
        </button>
      </div>
    </div>
  );
};

export type { FiltersState };
export default Filters;
