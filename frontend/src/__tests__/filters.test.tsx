import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import { vi } from "vitest";
import Filters, { FiltersState } from "../components/Filters";

const baseFilters: FiltersState = {
  vin: "",
  manufacture: "",
  model: "",
  yearMin: "",
  yearMax: "",
  priceMin: "",
  priceMax: "",
  mileageMin: "",
  mileageMax: "",
  color: "",
  status: "",
  conditionGrade: "",
  packageIds: [],
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
