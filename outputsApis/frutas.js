console.log('Servidor iniciado correctamente. Bienvenido a la API dinámica.');

//----INFORMACION SERVIDOR LOCAL CON NODE Y EXPRESS----
const express = require('express');
const app = express();
app.use(express.json());

const frutaList = [
    {
        id: 1,
        nombre: "fb02f6e0",
        color: "fc3b6bc6",
        precio: 0.6481178119995268,
    },
    {
        id: 2,
        nombre: "b2757bae",
        color: "8a3e4840",
        precio: 27.827912399332966,
    },
    {
        id: 3,
        nombre: "19b11d8e",
        color: "78147e83",
        precio: 73.39245221727762,
    },
];

const carroList = [
    {
        id: 1,
        nombrecar: "424b1418",
        colorcar: "b8221980",
        preciocar: 90.8778331775232,
    },
    {
        id: 2,
        nombrecar: "b7507aec",
        colorcar: "26bd9e9e",
        preciocar: 66.39453670340023,
    },
    {
        id: 3,
        nombrecar: "a65f6fc8",
        colorcar: "f028be9e",
        preciocar: 85.08058850022628,
    },
];

//----INFORMACION GENERAL DE LA API----
//Se hace un GET general sobre '/frutas'
app.get('/frutas', (req, res) => {
    res.status(200).json(frutaList);
});

//Se hace un GET individual sobre '/fruta'
app.get('/fruta/:id', (req, res) => {
    const item = frutaList.find(i => i.id === parseInt(req.params.id));
    if (item) res.status(200).json(item);
    else res.status(404).json({ message: 'fruta no encontrado' });
});

//Se hace un POST sobre '/fruta'
app.post('/fruta', (req, res) => {
    delete req.body.id;
    const newItem = { id: frutaList.length + 1, ...req.body };
    if (Object.keys(newItem).length < 4) {
        res.status(400).json('Faltan campos requeridos');
    } else {
        frutaList.push(newItem);
        res.status(201).json(newItem);
    }
});

//Se hace un PUT individual sobre '/fruta'
app.put('/fruta/:id', (req, res) => {
    const itemIndex = frutaList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        frutaList[itemIndex] = { id: frutaList[itemIndex].id, ...req.body };
        res.json(frutaList[itemIndex]);
    } else {
        res.status(404).json({ message: 'fruta no encontrado' });
    }
});

//Se hace un DELETE individual sobre '/frutas'
app.delete('/fruta/:id', (req, res) => {
    const itemIndex = frutaList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        frutaList.splice(itemIndex, 1);
        res.status(200).json({message: 'fruta Eliminado'});
    } else {
        res.status(404).json({ message: 'fruta no encontrado' });
    }
});

//----INFORMACION GENERAL DE LA API----
//Se hace un GET general sobre '/carros'
app.get('/carros', (req, res) => {
    res.status(200).json(carroList);
});

//Se hace un GET individual sobre '/carro'
app.get('/carro/:id', (req, res) => {
    const item = carroList.find(i => i.id === parseInt(req.params.id));
    if (item) res.status(200).json(item);
    else res.status(404).json({ message: 'carro no encontrado' });
});

//Se hace un POST sobre '/carro'
app.post('/carro', (req, res) => {
    delete req.body.id;
    const newItem = { id: carroList.length + 1, ...req.body };
    if (Object.keys(newItem).length < 4) {
        res.status(400).json('Faltan campos requeridos');
    } else {
        carroList.push(newItem);
        res.status(201).json(newItem);
    }
});

//Se hace un PUT individual sobre '/carro'
app.put('/carro/:id', (req, res) => {
    const itemIndex = carroList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        carroList[itemIndex] = { id: carroList[itemIndex].id, ...req.body };
        res.json(carroList[itemIndex]);
    } else {
        res.status(404).json({ message: 'carro no encontrado' });
    }
});

//Se hace un DELETE individual sobre '/carros'
app.delete('/carro/:id', (req, res) => {
    const itemIndex = carroList.findIndex(i => i.id === parseInt(req.params.id));
    if (itemIndex !== -1) {
        carroList.splice(itemIndex, 1);
        res.status(200).json({message: 'carro Eliminado'});
    } else {
        res.status(404).json({ message: 'carro no encontrado' });
    }
});


app.listen(8080, () => console.log('Servidor corriendo en http://localhost:8080/'));
