import React from "react";
import { Vehicle } from "../types/inventory";
import { Link } from "react-router-dom";

type Props = {
  vehicles: Vehicle[];
};

const VehicleTable: React.FC<Props> = ({ vehicles }) => {
  return (
    <table className="table">
      <thead>
        <tr>
          <th>VIN</th>
          <th>Manufacture</th>
          <th>Model</th>
          <th>Year</th>
          <th>Color</th>
          <th>Mileage</th>
          <th>Status</th>
          <th>List Price</th>
          <th>Location</th>
        </tr>
      </thead>
      <tbody>
        {vehicles.map((vehicle) => (
          <tr key={vehicle.id}>
            <td>
              <Link to={`/vehicles/${vehicle.id}`}>{vehicle.vin}</Link>
            </td>
            <td>{vehicle.manufacture}</td>
            <td>{vehicle.model}</td>
            <td>{vehicle.year}</td>
            <td>{vehicle.color}</td>
            <td>{vehicle.mileage}</td>
            <td>
              <span className="badge">{vehicle.status}</span>
            </td>
            <td>${vehicle.list_price}</td>
            <td>{vehicle.location}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default VehicleTable;
