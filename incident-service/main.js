const express = require('express');
const cors = require('cors');
const app = express();

const PORT = 5001; // Runs on Port 5001

app.use(cors());
app.use(express.json());

let idCounter = 100;

// Catches submissions from your index.html form
app.post('/api/incidents', (req, res) => {
    const disasterType = req.body.type;
    const location = req.body.location;

    idCounter++; // Generate incremental unique incident ID

    console.log(`🚨 EMERGENCY LOGGED: ${disasterType} reported at ${location} (Assigned ID: ${idCounter})`);

    res.json({
        id: idCounter,
        status: "Success"
    });
});

app.listen(PORT, () => {
    console.log(`🚀 Incident Microservice is running on http://localhost:${PORT}`);
});