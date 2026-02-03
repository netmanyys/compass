export type Package = {
  id: string;
  name: string;
  description?: string;
};

export type Vehicle = {
  id: string;
  vin: string;
  manufacture: string;
  model: string;
  year: number;
  color: string;
  mileage: number;
  status: string;
  listPrice?: string;
  location: string;
  conditionGrade?: string;
  carfaxUrl?: string;
  notes?: string;
  primaryImageUrl?: string;
  packages?: Package[];
  serviceRecords?: ServiceRecord[];
  images?: VehicleImage[];
  comments?: VehicleComment[];
};

export type ServiceRecord = {
  id: string;
  vehicleId?: string;
  service_date: string;
  vendor: string;
  odometer?: number;
  category: string;
  description: string;
  cost: string;
  invoice_url?: string;
};

export type VehicleImage = {
  id: string;
  vehicleId?: string;
  imageUrl?: string;
  image_url: string;
  is_primary: boolean;
};

export type VehicleComment = {
  id: string;
  vehicleId?: string;
  comment: string;
  created_at: string;
};

export type UserMe = {
  id: string;
  username: string;
  email: string;
  role: "admin" | "staff";
};
