import React, { useState } from "react";

const CustomerDetailsCard = ({ onChange }) => {
     const [name, setName] = useState("");
     const [mobile, setMobile] = useState("");
     const [address, setAddress] = useState("");
     const [paymentMode, setPaymentMode] = useState("CASH");

     const handleInputChange = (field, value) => {
          if (field === "name") setName(value);
          if (field === "mobile") setMobile(value);
          if (field === "address") setAddress(value);
          if (field === "paymentMode") setPaymentMode(value);

          if (onChange) {
               onChange({ name, mobile, address, paymentMode });
          }
     };

     return (
          <div className="bg-white shadow-md rounded-lg p-6 mb-6 max-w-md">
               <h2 className="text-xl font-semibold mb-4">Customer Details</h2>
               <div className="mb-4">
                    <label htmlFor="customerName" className="block mb-2 font-medium text-gray-700">
                         Name
                    </label>
                    <input
                         id="customerName"
                         type="text"
                         value={name}
                         onChange={(e) => handleInputChange("name", e.target.value)}
                         placeholder="Enter customer name"
                         className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
               </div>
               <div className="mb-4">
                    <label htmlFor="customerMobile" className="block mb-2 font-medium text-gray-700">
                         Mobile Number
                    </label>
                    <input
                         id="customerMobile"
                         type="tel"
                         value={mobile}
                         onChange={(e) => handleInputChange("mobile", e.target.value)}
                         placeholder="Enter mobile number"
                         maxLength={10}
                         className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
               </div>
               <div className="mb-4">
                    <label htmlFor="customerAddress" className="block mb-2 font-medium text-gray-700">
                         Address
                    </label>
                    <input
                         id="customerAddress"
                         type="text"
                         value={address}
                         onChange={(e) => handleInputChange("address", e.target.value)}
                         placeholder="Enter Address"
                         className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    />
               </div>
               <fieldset className="mb-4">
                    <legend className="block mb-2 font-medium text-gray-700">Payment Mode</legend>
                    <div className="flex space-x-6">
                         <label className="inline-flex items-center">
                              <input
                                   type="radio"
                                   name="paymentMode"
                                   value="CASH"
                                   checked={paymentMode === "CASH"}
                                   onChange={() => handleInputChange("paymentMode", "CASH")}
                                   className="form-radio text-indigo-600"
                              />
                              <span className="ml-2 text-gray-700">Cash</span>
                         </label>
                         <label className="inline-flex items-center">
                              <input
                                   type="radio"
                                   name="paymentMode"
                                   value="ONLINE"
                                   checked={paymentMode === "ONLINE"}
                                   onChange={() => handleInputChange("paymentMode", "ONLINE")}
                                   className="form-radio text-indigo-600"
                              />
                              <span className="ml-2 text-gray-700">Online</span>
                         </label>
                    </div>
               </fieldset>
          </div>
     );
};

export default CustomerDetailsCard;
