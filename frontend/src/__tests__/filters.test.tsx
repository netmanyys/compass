import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { vi } from "vitest";
import Filters, { FiltersState } from "../components/Filters";

const baseFilters: FiltersState = {
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

test("Filters updates values and triggers callbacks", () => {
  const onChange = vi.fn();
  const onSubmit = vi.fn();
  const onReset = vi.fn();

  render(
    <Filters
      filters={baseFilters}
      packages={[{ id: 1, name: "Premium" }]}
      onChange={onChange}
      onSubmit={onSubmit}
      onReset={onReset}
    />
  );

  const vinInput = screen.getByPlaceholderText("VIN (exact)");
  fireEvent.change(vinInput, { target: { value: "ABC" } });
  expect(onChange).toHaveBeenCalled();

  fireEvent.click(screen.getByText("Apply Filters"));
  expect(onSubmit).toHaveBeenCalled();

  fireEvent.click(screen.getByText("Reset"));
  expect(onReset).toHaveBeenCalled();
});
