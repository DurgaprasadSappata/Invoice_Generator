import React from "react";

const SelectedItemsCard = ({ selectedItems, removeItem }) => {
     return (
          <div className="bg-white shadow-md rounded-lg p-6 mb-6 overflow-auto">
               <h2 className="text-xl font-semibold mb-4">Selected Items</h2>
               {selectedItems.length === 0 ? (
                    <p className="text-gray-600">No items selected.</p>
               ) : (
                    <table className="min-w-full border border-gray-300">
                         <thead className="bg-indigo-50">
                              <tr>
                                   <th className="px-3 py-2 text-left">Sr.No</th>
                                   <th className="px-3 py-2 text-left">Product Name</th>
                                   <th className="px-3 py-2 text-left">HSN</th>
                                   <th className="px-3 py-2 text-right">Qty</th>
                                   <th className="px-3 py-2 text-right">Rate</th>
                                   <th className="px-3 py-2 text-right">Disc.</th>
                                   <th className="px-3 py-2 text-right">Taxable Amt.</th>
                                   <th className="px-3 py-2 text-right">CGST %</th>
                                   <th className="px-3 py-2 text-right">SGST %</th>
                                   <th className="px-3 py-2 text-right">IGST %</th>
                                   <th className="px-3 py-2 text-center">Amount</th>
                              </tr>
                         </thead>
                         <tbody>
                              {selectedItems.map((item, index) => (
                                   <tr key={item.tagId} className="border-t border-gray-300">
                                        <td className="px-3 py-2 text-left whitespace-nowrap">{index + 1}</td>
                                        <td className="px-3 py-2 text-left whitespace-nowrap">{item.name ?? "-"}</td>
                                        <td className="px-3 py-2 text-left whitespace-nowrap">{item.hsnCode ?? "-"}</td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">{item.quantity ?? 0}</td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">₹{(item.price ?? 0).toFixed(2)}</td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">{item.discount ?? 0}</td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">
                                             ₹
                                             {(
                                                  ((item.price ?? 0) * (item.quantity ?? 0)) -
                                                  (((item.price ?? 0) * (item.quantity ?? 0) * (item.discount ?? 0)) / 100)
                                             ).toFixed(2)}
                                        </td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">{item.cgstRate ?? 0}</td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">{item.sgstRate ?? 0}</td>
                                        <td className="px-3 py-2 text-right whitespace-nowrap">{item.igstRate ?? 0}</td>
                                        <td className="px-3 py-2 text-center whitespace-nowrap">
                                             <button
                                                  onClick={() => removeItem(item.tagId)}
                                                  className="text-red-600 hover:text-red-800 font-semibold"
                                                  title="Remove Item"
                                             >
                                                  &times;
                                             </button>
                                        </td>
                                   </tr>
                              ))}
                         </tbody>
                    </table>
               )}
          </div>
     );
};

export default SelectedItemsCard;

