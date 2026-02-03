import React from "react";
import { render, screen } from "@testing-library/react";
import VehicleTable from "../components/VehicleTable";
import { MemoryRouter } from "react-router-dom";

const vehicles = [
  {
    id: "1",
    vin: "VIN123",
    manufacture: "Toyota",
    model: "Camry",
    year: 2020,
    color: "Red",
    mileage: 20000,
    status: "IN_STOCK",
    listPrice: "20000",
    location: "Lot A",
    condition_grade: "A",
  },
];

test("VehicleTable renders vehicle cards", () => {
  render(
    <MemoryRouter>
      <VehicleTable vehicles={vehicles} />
    </MemoryRouter>
  );

  expect(screen.getByText("2020 Toyota Camry")).toBeInTheDocument();
  expect(screen.getByText("Red • 20000 miles")).toBeInTheDocument();
  expect(screen.getByText("View details →")).toBeInTheDocument();
});
