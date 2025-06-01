import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

const InvoicePdfPage = () => {
     const location = useLocation();
     const { customerDetails, selectedItems } = location.state || {};
     const [pdfUrl, setPdfUrl] = useState("");
     const [email, setEmail] = useState("");

     useEffect(() => {
          const fetchPdf = async () => {
               try {
                    const payload = {
                         customerName: customerDetails?.name || "",
                         customerAddress: customerDetails?.address || "",
                         customerPhone: customerDetails?.mobile || "",
                         paymentMode: customerDetails?.paymentMode || "",
                         items: selectedItems || [],
                    };
                    console.log("Payload:", payload);

                    const response = await fetch("http://localhost:8080/api/invoice/generate", {
                         method: "POST",
                         headers: {
                              "Content-Type": "application/json",
                         },
                         body: JSON.stringify(payload),
                    });

                    if (!response.ok) {
                         throw new Error("Failed to generate PDF");
                    }

                    const blob = await response.blob();
                    const url = URL.createObjectURL(blob);
                    setPdfUrl(url);
               } catch (error) {
                    alert("Error fetching PDF: " + error.message);
               }
          };

          if (
               customerDetails &&
               selectedItems &&
               selectedItems.length > 0 &&
               !pdfUrl
          ) {
               fetchPdf();
          }
          return () => {
               if (pdfUrl) {
                    URL.revokeObjectURL(pdfUrl);
               }
          };
     }, [customerDetails, selectedItems, pdfUrl]);

     const handleDownload = () => {
          if (!pdfUrl) return;
          const link = document.createElement("a");
          link.href = pdfUrl;
          link.setAttribute("download", "invoice.pdf");
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
     };

     const handleSendEmail = async () => {
          if (!email) {
               alert("Please enter an email address.");
               return;
          }
          const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
          if (!emailPattern.test(email)) {
               alert("Please enter a valid email address.");
               return;
          }
          try {
               await fetch("http://localhost:8080/api/invoice/send-email", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, customerDetails, selectedItems }),
               });
               alert("Email sent successfully!");
          } catch (error) {
               alert("Error sending email: " + error.message);
          }
     };

     return (
          <div className="max-w-7xl mx-auto p-6">
               <h1 className="text-4xl font-bold text-center mb-8">Invoice PDF</h1>
               {pdfUrl ? (
                    <div className="flex flex-col items-center">
                         <iframe
                              src={pdfUrl}
                              title="Invoice PDF"
                              className="w-full h-[600px] border border-gray-300"
                         />
                         <div className="mt-4 flex flex-col md:flex-row md:items-center md:space-x-4 space-y-4 md:space-y-0 w-full max-w-lg">
                              <button
                                   onClick={handleDownload}
                                   className="bg-indigo-600 text-white font-semibold py-2 px-4 rounded-md hover:bg-indigo-700 transition md:flex-shrink-0"
                              >
                                   Download / Print
                              </button>
                              <input
                                   type="email"
                                   placeholder="Enter email to send"
                                   value={email}
                                   onChange={(e) => setEmail(e.target.value)}
                                   className="border border-gray-300 rounded-md p-2 flex-grow focus:outline-none focus:ring-2 focus:ring-indigo-500"
                              />
                              <button
                                   onClick={handleSendEmail}
                                   className="bg-green-600 text-white font-semibold py-2 px-4 rounded-md hover:bg-green-700 transition md:flex-shrink-0"
                              >
                                   Send to Email
                              </button>
                         </div>
                    </div>
               ) : (
                    <p className="text-gray-500 text-center">Generating PDF...</p>
               )}
          </div>
     );
};

export default InvoicePdfPage;
