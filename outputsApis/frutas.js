//----INFORMACION GENERAL DE LA API----

//Info_Title: API de Frutas
//Info_Description:Esta API permite gestionar información sobre frutas.
//Info_Version: 1.0.0
//Info_TermsOfService: null

//----INFORMACION SERVIDOR LOCAL CON NODE Y EXPRESS----
const express = require('express');
const app = express();
app.use(express.json());

const frutaSchema = {
properties: {
        id: 'integer',
        nombre: 'string',
        color: 'string',
        precio: 'number',
    }
};

const carroSchema = {
properties: {
        idcar: 'integer',
        nombrecar: 'string',
        colorcar: 'string',
        preciocar: 'number',
    }
};

//-------------ENDPOINS DEL OBJETO fruta-------------

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

//-------------ENDPOINS DEL OBJETO carro-------------

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
