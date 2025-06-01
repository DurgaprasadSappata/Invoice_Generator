import React, { useState } from "react";

const AddItemCard = ({ allItems, addItem }) => {
     const [selectedTagId, setSelectedTagId] = useState("");
     const [quantity, setQuantity] = useState(1);
     const [discount, setDiscount] = useState(10);
     const [cgstRate, setCgstRate] = useState(6);
     const [sgstRate, setSgstRate] = useState(6);
     const [igstRate, setIgstRate] = useState(0);

     const handleAddClick = () => {
          if (!selectedTagId) {
               alert("Please select an item.");
               return;
          }
          addItem({ tagId: selectedTagId, quantity, discount, cgstRate, sgstRate, igstRate });

          // Reset inputs after adding
          setSelectedTagId("");
          setQuantity(1);
          setDiscount(10);
          setCgstRate(6);
          setSgstRate(6);
          setIgstRate(0);
     };

     return (
          <div className="bg-white shadow-md rounded-lg p-6 mb-6">
               <h2 className="text-xl font-semibold mb-4">Add Item</h2>
               <div className="mb-4">
                    <label className="block mb-2">Select Item</label>
                    <select
                         value={selectedTagId}
                         onChange={(e) => setSelectedTagId(e.target.value)}
                         className="w-full border border-gray-300 rounded-md p-2"
                    >
                         <option value="">Select an item</option>
                         {allItems.map((item) => (
                              <option key={item.tagId} value={item.tagId}>
                                   {item.name} (Tag ID: {item.tagId})
                              </option>
                         ))}
                    </select>
               </div>
               <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                         <label className="block mb-2">Quantity</label>
                         <input
                              type="number"
                              min="1"
                              value={quantity}
                              onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))}
                              className="w-full border border-gray-300 rounded-md p-2"
                         />
                    </div>
                    <div>
                         <label className="block mb-2">Discount (%)</label>
                         <input
                              type="number"
                              min="0"
                              max="100"
                              value={discount}
                              onChange={(e) => setDiscount(Math.min(Math.max(0, Number(e.target.value)), 100))}
                              className="w-full border border-gray-300 rounded-md p-2"
                         />
                    </div>
               </div>
               <div className="grid grid-cols-3 gap-4 mb-4">
                    <div>
                         <label className="block mb-2">CGST (%)</label>
                         <input
                              type="number"
                              min="0"
                              max="100"
                              value={cgstRate}
                              onChange={(e) => setCgstRate(Math.min(Math.max(0, Number(e.target.value)), 100))}
                              className="w-full border border-gray-300 rounded-md p-2"
                         />
                    </div>
                    <div>
                         <label className="block mb-2">SGST (%)</label>
                         <input
                              type="number"
                              min="0"
                              max="100"
                              value={sgstRate}
                              onChange={(e) => setSgstRate(Math.min(Math.max(0, Number(e.target.value)), 100))}
                              className="w-full border border-gray-300 rounded-md p-2"
                         />
                    </div>
                    <div>
                         <label className="block mb-2">IGST (%)</label>
                         <input
                              type="number"
                              min="0"
                              max="100"
                              value={igstRate}
                              onChange={(e) => setIgstRate(Math.min(Math.max(0, Number(e.target.value)), 100))}
                              className="w-full border border-gray-300 rounded-md p-2"
                         />
                    </div>
               </div>
               <button
                    onClick={handleAddClick}
                    className="w-full bg-indigo-600 text-white font-semibold py-2 rounded-md hover:bg-indigo-700 transition"
               >
                    Add Item
               </button>
          </div>
     );
};

export default AddItemCard;
