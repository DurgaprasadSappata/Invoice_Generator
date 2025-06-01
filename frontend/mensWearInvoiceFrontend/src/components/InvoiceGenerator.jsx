import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import AddItemCard from "./cards/AddItemCard";
import SelectedItemsCard from "./cards/SelectedItemsCard";
import InvoiceTable from "./InvoiceTable";
import CustomerDetailsCard from "./cards/CustomerDetailsCard";

const InvoiceGenerator = () => {
     const navigate = useNavigate();
     const [allItems, setAllItems] = useState([]);
     const [loading, setLoading] = useState(true);
     const [error, setError] = useState(null);
     const [selectedItems, setSelectedItems] = useState([]);
     const [totals, setTotals] = useState({});
     const [calculatingInvoice, setCalculatingInvoice] = useState(false);
     const [customerDetails, setCustomerDetails] = useState({
          name: "",
          mobile: "",
          address: "",
          paymentMode: "CASH",
     });

     useEffect(() => {
          const fetchItems = async () => {
               try {
                    const res = await fetch("http://localhost:8080/api/items");
                    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
                    const data = await res.json();
                    setAllItems(data);
                    setLoading(false);
                    setError(null);
               } catch (err) {
                    setError("Failed to fetch items, please try again.");
                    setLoading(false);
               }
          };
          fetchItems();
     }, []);

     const addItem = async ({ tagId, quantity, discount, cgstRate, sgstRate, igstRate }) => {
          try {
               if (selectedItems.some((item) => item.tagId === tagId)) {
                    alert("Item already added.");
                    return;
               }
               const response = await fetch(`http://localhost:8080/api/items/${tagId}`);
               if (!response.ok) throw new Error(`Failed to fetch item with tagId ${tagId}`);
               const itemDetails = await response.json();
               const newItem = {
                    tagId,
                    name: itemDetails.name,
                    hsnCode: itemDetails.hsnCode,
                    price: itemDetails.price,
                    quantity,
                    discount,
                    cgstRate,
                    sgstRate,
                    igstRate,
               };
               setSelectedItems((prev) => [...prev, newItem]);
          } catch (error) {
               alert("Error adding item: " + error.message);
          }
     };

     const removeItem = (tagId) => {
          setSelectedItems((prev) => prev.filter((item) => item.tagId !== tagId));
     };

     // Calculate invoice totals
     const handleCalculateInvoice = async () => {
          if (selectedItems.length === 0) {
               alert("Please add at least one item.");
               return;
          }
          setCalculatingInvoice(true);
          try {
               const response = await fetch("http://localhost:8080/api/items/invoice/calculate", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(
                         selectedItems.map(({ tagId, quantity, discount, cgstRate, sgstRate, igstRate }) => ({
                              tagId,
                              quantity,
                              discount,
                              cgstRate,
                              sgstRate,
                              igstRate,
                         }))
                    ),
               });
               if (!response.ok) throw new Error(`Failed to calculate invoice: ${response.statusText}`);
               const data = await response.json();
               setTotals(data);
          } catch (error) {
               alert("Error calculating invoice: " + error.message);
          } finally {
               setCalculatingInvoice(false);
          }
     };

     const handleGenerateInvoicePdf = () => {
          if (!totals || Object.keys(totals).length === 0) {
               alert("Please calculate the invoice first.");
               return;
          }
          navigate("/invoice-pdf", { state: { customerDetails, selectedItems } });
     };

     return (
          <div className="max-w-7xl mx-auto p-6">
               <h1 className="text-4xl font-bold text-center mb-8 text-gray-900">Men's Wear Shop - Invoice Generator</h1>

               <div className="flex flex-col md:flex-row md:space-x-6 mb-6">
                    <div className="md:w-1/3">
                         <CustomerDetailsCard onChange={setCustomerDetails} />
                    </div>
                    <div className="md:w-2/3 mt-6 md:mt-0">
                         <AddItemCard allItems={allItems} addItem={addItem} />
                    </div>
               </div>

               {loading ? (
                    <p className="text-center text-gray-500">Loading items...</p>
               ) : error ? (
                    <p className="text-center text-red-500">{error}</p>
               ) : (
                    <>
                         <SelectedItemsCard selectedItems={selectedItems} removeItem={removeItem} />

                         <button
                              onClick={handleCalculateInvoice}
                              disabled={calculatingInvoice}
                              className={`w-full bg-indigo-600 text-white font-semibold py-2 rounded-md hover:bg-indigo-700 transition mb-4 ${calculatingInvoice ? "opacity-70 cursor-not-allowed" : ""}`}
                         >
                              {calculatingInvoice ? "Calculating..." : "Calculate Invoice"}
                         </button>

                         {Object.keys(totals).length > 0 && <InvoiceTable totals={totals} />}

                         {Object.keys(totals).length > 0 && (
                              <button
                                   onClick={handleGenerateInvoicePdf}
                                   className="w-full bg-green-600 text-white font-semibold py-2 rounded-md hover:bg-green-700 transition mt-4"
                              >
                                   Generate Invoice PDF
                              </button>
                         )}
                    </>
               )}
          </div>
     );
};

export default InvoiceGenerator;
