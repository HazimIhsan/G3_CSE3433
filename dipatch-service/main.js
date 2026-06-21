const express = require('express');
const cors = require('cors');
const app = express();

// This service runs on Port 5003!
const PORT = 5003;

app.use(cors());
app.use(express.json());

// This catches Form B submissions from the frontend dashboard
app.post('/api/dispatch', async (req, res) => {
    const incidentId = req.body.incidentId;
    const item = req.body.item;
    const quantity = req.body.quantity;

    console.log(`✈️ DISPATCH RECEIVED: Attempting to send ${quantity} [${item}] to Incident #${incidentId}...`);

    try {
        // MICROSERVICES INTERACTION TRICK:
        // We use native fetch to send a command to the Resource Service on Port 5002 to deduct stock!
        const resourceResponse = await fetch('http://localhost:5002/api/resources/deduct', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ item: item, quantity: quantity })
        });
        
        const resourceData = await resourceResponse.json();

        if (resourceResponse.ok) {
            console.log(`✅ SUCCESS: Resource service deducted items. Remaining stock: ${resourceData.remaining}`);
            res.json({ message: `Mission launched successfully! Warehouse remaining: ${resourceData.remaining}` });
        } else {
            res.status(400).json({ message: `Failed: ${resourceData.message}` });
        }

    } catch (error) {
        console.error("❌ Link Error:", error.message);
        res.status(500).json({ message: "Could not contact Resource Service." });
    }
});

app.listen(PORT, () => {
    console.log(`✈️ Dispatch Orchestrator is running on http://localhost:${PORT}`);
});