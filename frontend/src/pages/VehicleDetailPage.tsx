import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/client";
import { Vehicle } from "../types/inventory";
import VehicleDetailPanel from "../components/VehicleDetailPanel";
import { useAuth } from "../context/AuthContext";

const VehicleDetailPage: React.FC = () => {
  const { id } = useParams();
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();

  useEffect(() => {
    const fetchVehicle = async () => {
      setLoading(true);
      try {
        const response = await api.get(`/vehicles/${id}/`);
        setVehicle(response.data);
      } finally {
        setLoading(false);
      }
    };
    fetchVehicle();
  }, [id]);

  return (
    <div className="container">
      {loading ? <p>Loading...</p> : <VehicleDetailPanel vehicle={vehicle} isAdmin={user?.role === "admin"} />}
    </div>
  );
};

export default VehicleDetailPage;
