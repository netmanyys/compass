import React from "react";
import { Package } from "../types/inventory";

type FiltersState = {
  vin: string;
  manufacture: string;
  model: string;
  yearMin: string;
  yearMax: string;
  priceMin: string;
  priceMax: string;
  mileageMin: string;
  mileageMax: string;
  color: string;
  status: string;
  conditionGrade: string;
  packageIds: string[];
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
    const next = filters.packageIds.includes(id)
      ? filters.packageIds.filter((pkg) => pkg !== id)
      : [...filters.packageIds, id];
    update("packageIds", next);
  };

  return (
    <div>
      <div className="filter-group">
        <div className="filter-title">Make & Model</div>
        <div className="form-row">
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
        </div>
      </div>

      <div className="filter-group">
        <div className="filter-title">VIN</div>
        <input
          placeholder="VIN (exact)"
          value={filters.vin}
          onChange={(e) => update("vin", e.target.value)}
        />
      </div>

      <div className="filter-group">
        <div className="filter-title">Year</div>
        <div className="form-row">
          <input
            placeholder="Year Min"
            value={filters.yearMin}
            onChange={(e) => update("yearMin", e.target.value)}
          />
          <input
            placeholder="Year Max"
            value={filters.yearMax}
            onChange={(e) => update("yearMax", e.target.value)}
          />
        </div>
      </div>

      <div className="filter-group">
        <div className="filter-title">Price</div>
        <div className="form-row">
          <input
            placeholder="Price Min"
            value={filters.priceMin}
            onChange={(e) => update("priceMin", e.target.value)}
          />
          <input
            placeholder="Price Max"
            value={filters.priceMax}
            onChange={(e) => update("priceMax", e.target.value)}
          />
        </div>
      </div>

      <div className="filter-group">
        <div className="filter-title">Mileage</div>
        <div className="form-row">
          <input
            placeholder="Mileage Min"
            value={filters.mileageMin}
            onChange={(e) => update("mileageMin", e.target.value)}
          />
          <input
            placeholder="Mileage Max"
            value={filters.mileageMax}
            onChange={(e) => update("mileageMax", e.target.value)}
          />
        </div>
      </div>

      <div className="filter-group">
        <div className="filter-title">Details</div>
        <div className="form-row">
          <input
            placeholder="Color"
            value={filters.color}
            onChange={(e) => update("color", e.target.value)}
          />
          <input
            placeholder="Condition Grade"
            value={filters.conditionGrade}
            onChange={(e) => update("conditionGrade", e.target.value)}
          />
          <input
            placeholder="Status"
            value={filters.status}
            onChange={(e) => update("status", e.target.value)}
          />
        </div>
      </div>

      <div className="filter-group">
        <div className="filter-title">Packages</div>
        <div className="form-row">
          {packages.map((pkg) => (
            <label key={pkg.id}>
              <input
                type="checkbox"
                checked={filters.packageIds.includes(String(pkg.id))}
                onChange={() => togglePackage(String(pkg.id))}
              />
              {pkg.name}
            </label>
          ))}
        </div>
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
