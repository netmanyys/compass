import React from "react";
import { Vehicle } from "../types/inventory";

const VehicleDetailPanel: React.FC<{ vehicle: Vehicle | null; isAdmin: boolean }> = ({ vehicle, isAdmin }) => {
  if (!vehicle) return null;

  const apiBase = (import.meta as any).env?.VITE_API_BASE || "http://localhost:8080/api";
  const uploadBase = apiBase.replace(/\/api\/?$/, "");

  return (
    <div className="grid two">
      <div className="card">
        <h3>{vehicle.manufacture} {vehicle.model} ({vehicle.year})</h3>
        <p>VIN: {vehicle.vin}</p>
        <p>Color: {vehicle.color}</p>
        <p>Mileage: {vehicle.mileage}</p>
        <p>Status: {vehicle.status}</p>
        <p>Condition: {vehicle.conditionGrade ?? (vehicle as any).condition_grade}</p>
        <p>List Price: ${(vehicle as any).listPrice ?? (vehicle as any).list_price}</p>
        <p>Location: {vehicle.location}</p>
        {((vehicle as any).carfaxUrl ?? (vehicle as any).carfax_url) && (
          <p>
            Carfax: <a href={(vehicle as any).carfaxUrl ?? (vehicle as any).carfax_url} target="_blank" rel="noreferrer">View</a>
          </p>
        )}
        {isAdmin && (
          <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
            <span className="badge">Admin actions below</span>
          </div>
        )}
      </div>

      <div className="card">
        <h4>Packages</h4>
        <div>
          {Array.isArray(vehicle.packages) && vehicle.packages.length > 0
            ? vehicle.packages.map((pkg: any) => <span key={pkg.id} className="badge" style={{ marginRight: 8 }}>{pkg.name}</span>)
            : "None"}
        </div>
        <h4 style={{ marginTop: 16 }}>Notes</h4>
        <p>{vehicle.notes || "No notes"}</p>
      </div>

      <div className="card">
        <h4>Service Records</h4>
        {vehicle.serviceRecords?.length ? (
          <ul>
            {vehicle.serviceRecords.map((record) => (
              <li key={record.id}>
                {(record as any).serviceDate ?? record.service_date} - {record.category} - ${record.cost}
              </li>
            ))}
          </ul>
        ) : (
          <p>No service records.</p>
        )}
        {isAdmin && <button className="btn" style={{ marginTop: 12 }}>Add Service Record</button>}
      </div>

      <div className="card">
        <h4>Comments</h4>
        {vehicle.comments?.length ? (
          <ul>
            {vehicle.comments.map((comment) => (
              <li key={comment.id}>{comment.comment}</li>
            ))}
          </ul>
        ) : (
          <p>No comments.</p>
        )}
        {isAdmin && <button className="btn" style={{ marginTop: 12 }}>Add Comment</button>}
      </div>

      <div className="card">
        <h4>Images</h4>
        {vehicle.images?.length ? (
          <div className="grid">
            {vehicle.images.map((img) => {
              const url = (img as any).imageUrl ?? img.image_url;
              const src = url && url.startsWith("/uploads") ? `${uploadBase}${url}` : url;
              return <img key={img.id} src={src} alt="vehicle" style={{ width: "100%", borderRadius: 8 }} />;
            })}
          </div>
        ) : (
          <p>No images.</p>
        )}
      </div>
    </div>
  );
};

export default VehicleDetailPanel;
