import React from "react";
import { render, screen } from "@testing-library/react";
import VehicleTable from "../components/VehicleTable";
import { MemoryRouter } from "react-router-dom";

const vehicles = [
  {
    id: 1,
    vin: "VIN123",
    manufacture: "Toyota",
    model: "Camry",
    year: 2020,
    color: "Red",
    mileage: 20000,
    status: "IN_STOCK",
    list_price: "20000",
    location: "Lot A",
    condition_grade: "A",
  },
];

test("VehicleTable renders vehicle rows", () => {
  render(
    <MemoryRouter>
      <VehicleTable vehicles={vehicles} />
    </MemoryRouter>
  );

  expect(screen.getByText("VIN123")).toBeInTheDocument();
  expect(screen.getByText("Toyota")).toBeInTheDocument();
  expect(screen.getByText("Camry")).toBeInTheDocument();
});
