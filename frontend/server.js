const express = require("express");
const axios = require("axios");
const path = require("path");

const app = express();

app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(express.static(path.join(__dirname, "public")));

const USER_SERVICE_URL = "http://localhost:8081";

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, "public", "index.html"));
});

app.post("/send-code", async (req, res) => {
    const { email } = req.body;

    try {
        await axios.post(`${USER_SERVICE_URL}/auth/request-code`, {
            email
        });

        res.redirect(`/verify?email=${encodeURIComponent(email)}`);
    } catch (error) {
        console.error("Erro ao solicitar código:", error.response?.data || error.message);
        res.status(500).send("Erro ao solicitar código. Tente novamente.");
    }
});

app.get("/verify", (req, res) => {
    res.sendFile(path.join(__dirname, "public", "verify.html"));
});

app.post("/verify-code", async (req, res) => {
    const { email, code } = req.body;

    try {
        const response = await axios.post(`${USER_SERVICE_URL}/auth/verify-code`, {
            email,
            code
        });

        const data = response.data;

        if (data.token) {
            return res.send(`
                <script>
                    sessionStorage.setItem("token", "${data.token}");
                    window.location.href = "/dashboard";
                </script>
            `);
        }

        if (data.valid === true) {
            return res.send(`
                <script>
                    alert("Código validado com sucesso!");
                    window.location.href = "/";
                </script>
            `);
        }

        return res.status(400).send("Código inválido ou expirado.");
    } catch (error) {
        console.error("Erro ao validar código:", error.response?.data || error.message);
        res.status(400).send("Código inválido ou expirado.");
    }
});

app.get("/dashboard", (req, res) => {
    res.send("<h1>Dashboard será implementado na Semana 4</h1>");
});

app.listen(3000, () => {
    console.log("Frontend rodando em http://localhost:3000");
});