export type Package = {
  id: number;
  name: string;
  description?: string;
};

export type Vehicle = {
  id: number;
  vin: string;
  manufacture: string;
  model: string;
  year: number;
  color: string;
  mileage: number;
  status: string;
  list_price: string;
  location: string;
  condition_grade: string;
  carfax_url?: string;
  notes?: string;
  packages?: Package[] | number[];
  service_records?: ServiceRecord[];
  images?: VehicleImage[];
  comments?: VehicleComment[];
};

export type ServiceRecord = {
  id: number;
  service_date: string;
  vendor: string;
  odometer?: number;
  category: string;
  description: string;
  cost: string;
  invoice_url?: string;
};

export type VehicleImage = {
  id: number;
  image_url: string;
  is_primary: boolean;
};

export type VehicleComment = {
  id: number;
  comment: string;
  created_at: string;
};

export type UserMe = {
  id: number;
  username: string;
  email: string;
  role: "admin" | "staff";
};
