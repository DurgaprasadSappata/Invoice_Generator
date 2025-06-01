import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from "react-router-dom"
import './App.css'
import InvoiceGenerator from './components/InvoiceGenerator'
import InvoicePdfPage from './components/InvoicePdfPage'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<InvoiceGenerator />} />
        <Route path="/invoice-pdf" element={<InvoicePdfPage />} />
      </Routes>
    </Router>
  )
}

export default App
