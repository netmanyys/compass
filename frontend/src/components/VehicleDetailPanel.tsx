import React from "react";
import { Vehicle } from "../types/inventory";

const VehicleDetailPanel: React.FC<{ vehicle: Vehicle | null; isAdmin: boolean }> = ({ vehicle, isAdmin }) => {
  if (!vehicle) return null;

  return (
    <div className="grid two">
      <div className="card">
        <h3>{vehicle.manufacture} {vehicle.model} ({vehicle.year})</h3>
        <p>VIN: {vehicle.vin}</p>
        <p>Color: {vehicle.color}</p>
        <p>Mileage: {vehicle.mileage}</p>
        <p>Status: {vehicle.status}</p>
        <p>Condition: {vehicle.condition_grade}</p>
        <p>List Price: ${vehicle.list_price}</p>
        <p>Location: {vehicle.location}</p>
        {vehicle.carfax_url && (
          <p>
            Carfax: <a href={vehicle.carfax_url} target="_blank" rel="noreferrer">View</a>
          </p>
        )}
        {isAdmin && (
          <div style={{ display: "flex", gap: 8, marginTop: 12 }}>
            <button className="btn">Edit</button>
            <button className="btn secondary">Delete</button>
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
        {vehicle.service_records?.length ? (
          <ul>
            {vehicle.service_records.map((record) => (
              <li key={record.id}>
                {record.service_date} - {record.category} - ${record.cost}
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
            {vehicle.images.map((img) => (
              <img key={img.id} src={img.image_url} alt="vehicle" style={{ width: "100%", borderRadius: 8 }} />
            ))}
          </div>
        ) : (
          <p>No images.</p>
        )}
      </div>
    </div>
  );
};

export default VehicleDetailPanel;
