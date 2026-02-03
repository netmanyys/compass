import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/client";
import { ServiceRecord, Vehicle, VehicleComment, VehicleImage } from "../types/inventory";
import VehicleDetailPanel from "../components/VehicleDetailPanel";
import { useAuth } from "../context/AuthContext";

const VehicleDetailPage: React.FC = () => {
  const { id } = useParams();
  const [vehicle, setVehicle] = useState<Vehicle | null>(null);
  const [serviceRecords, setServiceRecords] = useState<ServiceRecord[]>([]);
  const [comments, setComments] = useState<VehicleComment[]>([]);
  const [images, setImages] = useState<VehicleImage[]>([]);
  const [loading, setLoading] = useState(false);
  const { user } = useAuth();
  const [commentText, setCommentText] = useState("");
  const [serviceForm, setServiceForm] = useState({
    service_date: "",
    vendor: "",
    category: "MAINTENANCE",
    description: "",
    cost: "",
  });
  const [uploadForm, setUploadForm] = useState({
    caption: "",
    isPrimary: false,
    sortOrder: "0",
    file: null as File | null,
  });

  useEffect(() => {
    const fetchVehicle = async () => {
      setLoading(true);
      try {
        const response = await api.get(`/vehicles/${id}`);
        setVehicle(response.data);
        const [recordsRes, commentsRes, imagesRes] = await Promise.all([
          api.get("/service-records", { params: { vehicleId: id } }),
          api.get("/vehicle-comments", { params: { vehicleId: id } }),
          api.get("/vehicle-images", { params: { vehicleId: id } }),
        ]);
        setServiceRecords(recordsRes.data);
        setComments(commentsRes.data);
        setImages(imagesRes.data);
      } finally {
        setLoading(false);
      }
    };
    fetchVehicle();
  }, [id]);

  const refreshImages = async () => {
    const imagesRes = await api.get("/vehicle-images", { params: { vehicleId: id } });
    setImages(imagesRes.data);
  };

  const handleAddComment = async () => {
    if (!commentText.trim()) return;
    await api.post("/vehicle-comments", { vehicleId: id, comment: commentText });
    setCommentText("");
    const commentsRes = await api.get("/vehicle-comments", { params: { vehicleId: id } });
    setComments(commentsRes.data);
  };

  const handleAddServiceRecord = async () => {
    if (!serviceForm.service_date || !serviceForm.vendor || !serviceForm.description || !serviceForm.cost) return;
    await api.post("/service-records", {
      vehicleId: id,
      serviceDate: serviceForm.service_date,
      vendor: serviceForm.vendor,
      category: serviceForm.category,
      description: serviceForm.description,
      cost: serviceForm.cost,
    });
    setServiceForm({ service_date: "", vendor: "", category: "MAINTENANCE", description: "", cost: "" });
    const recordsRes = await api.get("/service-records", { params: { vehicleId: id } });
    setServiceRecords(recordsRes.data);
  };

  const handleUpload = async () => {
    if (!uploadForm.file) return;
    const formData = new FormData();
    formData.append("vehicleId", String(id));
    formData.append("image", uploadForm.file);
    if (uploadForm.caption) formData.append("caption", uploadForm.caption);
    formData.append("isPrimary", String(uploadForm.isPrimary));
    formData.append("sortOrder", uploadForm.sortOrder);
    await api.post("/vehicle-images", formData, { headers: { "Content-Type": "multipart/form-data" } });
    setUploadForm({ caption: "", isPrimary: false, sortOrder: "0", file: null });
    await refreshImages();
  };

  const handleDeleteVehicle = async () => {
    if (!id) return;
    await api.delete(`/vehicles/${id}`);
    window.location.href = "/dashboard";
  };

  const [editForm, setEditForm] = useState<any>(null);

  useEffect(() => {
    if (vehicle && !editForm) {
      setEditForm({
        vin: vehicle.vin,
        manufacture: vehicle.manufacture,
        model: vehicle.model,
        year: vehicle.year,
        color: vehicle.color,
        bodyType: (vehicle as any).bodyType || "Sedan",
        mileage: vehicle.mileage,
        conditionGrade: (vehicle as any).conditionGrade || "A",
        titleStatus: (vehicle as any).titleStatus || "Clean",
        status: vehicle.status,
        location: vehicle.location,
        purchasePrice: (vehicle as any).purchasePrice || "0",
        listPrice: (vehicle as any).listPrice || "0",
      });
    }
  }, [vehicle, editForm]);

  const handleUpdateVehicle = async () => {
    if (!id || !editForm) return;
    await api.patch(`/vehicles/${id}`, {
      vin: editForm.vin,
      manufacture: editForm.manufacture,
      model: editForm.model,
      year: Number(editForm.year),
      color: editForm.color,
      bodyType: editForm.bodyType,
      mileage: Number(editForm.mileage),
      conditionGrade: editForm.conditionGrade,
      titleStatus: editForm.titleStatus,
      status: editForm.status,
      location: editForm.location,
      purchasePrice: editForm.purchasePrice,
      listPrice: editForm.listPrice,
    });
    const response = await api.get(`/vehicles/${id}`);
    setVehicle(response.data);
  };

  return (
    <div className="container">
      {loading ? (
        <p>Loading...</p>
      ) : (
        <>
          <VehicleDetailPanel
            vehicle={{
              ...vehicle,
              serviceRecords,
              comments,
              images,
            } as Vehicle}
            isAdmin={user?.role === "admin"}
          />

          {user?.role === "admin" && (
            <div className="grid two" style={{ marginTop: 24 }}>
              <div className="card">
                <h4>Edit Vehicle</h4>
                {editForm && (
                  <>
                    <div className="form-row">
                      <input value={editForm.vin} onChange={(e) => setEditForm({ ...editForm, vin: e.target.value })} placeholder="VIN" />
                      <input value={editForm.manufacture} onChange={(e) => setEditForm({ ...editForm, manufacture: e.target.value })} placeholder="Manufacture" />
                      <input value={editForm.model} onChange={(e) => setEditForm({ ...editForm, model: e.target.value })} placeholder="Model" />
                      <input value={editForm.year} onChange={(e) => setEditForm({ ...editForm, year: e.target.value })} placeholder="Year" />
                      <input value={editForm.color} onChange={(e) => setEditForm({ ...editForm, color: e.target.value })} placeholder="Color" />
                      <input value={editForm.bodyType} onChange={(e) => setEditForm({ ...editForm, bodyType: e.target.value })} placeholder="Body Type" />
                    </div>
                    <div className="form-row">
                      <input value={editForm.mileage} onChange={(e) => setEditForm({ ...editForm, mileage: e.target.value })} placeholder="Mileage" />
                      <input value={editForm.conditionGrade} onChange={(e) => setEditForm({ ...editForm, conditionGrade: e.target.value })} placeholder="Condition Grade" />
                      <input value={editForm.titleStatus} onChange={(e) => setEditForm({ ...editForm, titleStatus: e.target.value })} placeholder="Title Status" />
                      <input value={editForm.status} onChange={(e) => setEditForm({ ...editForm, status: e.target.value })} placeholder="Status" />
                      <input value={editForm.location} onChange={(e) => setEditForm({ ...editForm, location: e.target.value })} placeholder="Location" />
                    </div>
                    <div className="form-row">
                      <input value={editForm.purchasePrice} onChange={(e) => setEditForm({ ...editForm, purchasePrice: e.target.value })} placeholder="Purchase Price" />
                      <input value={editForm.listPrice} onChange={(e) => setEditForm({ ...editForm, listPrice: e.target.value })} placeholder="List Price" />
                    </div>
                    <div style={{ display: "flex", gap: 8 }}>
                      <button className="btn" onClick={handleUpdateVehicle}>Save Changes</button>
                      <button className="btn secondary" onClick={handleDeleteVehicle}>Delete Vehicle</button>
                    </div>
                  </>
                )}
              </div>
              <div className="card">
                <h4>Add Comment</h4>
                <textarea
                  rows={3}
                  value={commentText}
                  onChange={(e) => setCommentText(e.target.value)}
                  placeholder="Add a comment"
                />
                <button className="btn" style={{ marginTop: 10 }} onClick={handleAddComment}>
                  Save Comment
                </button>
              </div>
              <div className="card">
                <h4>Add Service Record</h4>
                <div className="form-row">
                  <input
                    type="date"
                    value={serviceForm.service_date}
                    onChange={(e) => setServiceForm({ ...serviceForm, service_date: e.target.value })}
                  />
                  <input
                    placeholder="Vendor"
                    value={serviceForm.vendor}
                    onChange={(e) => setServiceForm({ ...serviceForm, vendor: e.target.value })}
                  />
                  <input
                    placeholder="Category"
                    value={serviceForm.category}
                    onChange={(e) => setServiceForm({ ...serviceForm, category: e.target.value })}
                  />
                  <input
                    placeholder="Cost"
                    value={serviceForm.cost}
                    onChange={(e) => setServiceForm({ ...serviceForm, cost: e.target.value })}
                  />
                </div>
                <textarea
                  rows={3}
                  placeholder="Description"
                  value={serviceForm.description}
                  onChange={(e) => setServiceForm({ ...serviceForm, description: e.target.value })}
                />
                <button className="btn" style={{ marginTop: 10 }} onClick={handleAddServiceRecord}>
                  Save Service Record
                </button>
              </div>
              <div className="card">
                <h4>Upload Image</h4>
                <div className="form-row">
                  <input
                    type="file"
                    onChange={(e) => setUploadForm({ ...uploadForm, file: e.target.files?.[0] ?? null })}
                  />
                  <input
                    placeholder="Caption"
                    value={uploadForm.caption}
                    onChange={(e) => setUploadForm({ ...uploadForm, caption: e.target.value })}
                  />
                  <input
                    placeholder="Sort Order"
                    value={uploadForm.sortOrder}
                    onChange={(e) => setUploadForm({ ...uploadForm, sortOrder: e.target.value })}
                  />
                </div>
                <label>
                  <input
                    type="checkbox"
                    checked={uploadForm.isPrimary}
                    onChange={(e) => setUploadForm({ ...uploadForm, isPrimary: e.target.checked })}
                  />
                  Set as primary
                </label>
                <button className="btn" style={{ marginTop: 10 }} onClick={handleUpload}>
                  Upload Image
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default VehicleDetailPage;
