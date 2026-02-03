import React from "react";
import { Vehicle } from "../types/inventory";
import { Link } from "react-router-dom";

type Props = {
  vehicles: Vehicle[];
};

const VehicleTable: React.FC<Props> = ({ vehicles }) => {
  const apiBase = (import.meta as any).env?.VITE_API_BASE || "http://localhost:8080/api";
  const uploadBase = apiBase.replace(/\/api\/?$/, "");
  return (
    <div className="vehicle-grid">
      {vehicles.map((vehicle) => {
        const listPrice = (vehicle as any).listPrice ?? (vehicle as any).list_price;
        const primaryImage = (vehicle as any).primaryImageUrl;
        const imageSrc = primaryImage
          ? primaryImage.startsWith("/uploads")
            ? `${uploadBase}${primaryImage}`
            : primaryImage
          : null;
        return (
          <div className="card vehicle-card" key={vehicle.id}>
            <div className="vehicle-image">
              {imageSrc ? (
                <img src={imageSrc} alt={vehicle.vin} />
              ) : (
                <div style={{ padding: 12, color: "#6b7280" }}>No image</div>
              )}
            </div>
            <div className="vehicle-body">
              <div className="vehicle-title">
                {vehicle.year} {vehicle.manufacture} {vehicle.model}
              </div>
              <div className="vehicle-sub">
                {vehicle.color} • {vehicle.mileage} miles
              </div>
              <div className="vehicle-price">${listPrice}</div>
              <div className="vehicle-meta">
                <span>{vehicle.location}</span>
                <span className="badge">{vehicle.status}</span>
              </div>
              <div style={{ marginTop: 10 }}>
                <Link to={`/vehicles/${vehicle.id}`}>View details →</Link>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default VehicleTable;
