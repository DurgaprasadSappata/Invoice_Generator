import React from "react";

const InvoiceTable = ({ totals }) => {
     return (
          <div className="bg-white shadow-md rounded-lg p-6 mb-6">
               <h2 className="text-xl font-semibold mb-4">Invoice Summary</h2>
               <div className="grid grid-cols-2 gap-4 text-gray-900 font-medium">
                    <div>Total Before Tax:</div>
                    <div>₹{totals.totalBeforeTax.toFixed(2)}</div>

                    <div>Total CGST:</div>
                    <div>₹{totals.totalCgst.toFixed(2)}</div>

                    <div>Total SGST:</div>
                    <div>₹{totals.totalSgst.toFixed(2)}</div>

                    <div>Total IGST:</div>
                    <div>₹{totals.totalIgst.toFixed(2)}</div>

                    <div>Total Tax:</div>
                    <div>₹{totals.totalTax.toFixed(2)}</div>

                    <div className="font-bold text-lg">Total After Tax:</div>
                    <div className="font-bold text-lg">₹{totals.totalAfterTax.toFixed(2)}</div>
               </div>
          </div>
     );
};

export default InvoiceTable;
