
document.addEventListener("DOMContentLoaded", () => {
    const loginContainer = document.getElementById("login-container");
    const appContainer = document.getElementById("dashboard");
    const btnLogin = document.getElementById("btnLogin");
    const btnMostrarFormulario = document.getElementById("btnMostrarFormulario");
    const btnGuardar = document.getElementById("btnGuardar");
    const usuarioInput = document.getElementById("username");
    const contrasenaInput = document.getElementById("password");
    const formularioProducto = document.getElementById("formularioProducto");
    const nombreInput = document.getElementById("nombre");
    const precioInput = document.getElementById("precio");
    const cantidadInput = document.getElementById("cantidad");
    const cuerpoTabla = document.getElementById("cuerpoTabla");
    const loginForm = document.getElementById("loginForm");
    const refreshBtn = document.querySelector(".btn-warning");
    const editBtn = document.getElementById("editBtn");
    const deleteBtn = document.getElementById("deleteBtn");

    let editandoId = null;

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const usuario = usuarioInput.value;
        const contrasena = contrasenaInput.value;

        const response = await fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usuario, contrasena })
        });

        if (response.ok) {
            document.getElementById("loginScreen").style.display = "none";
            document.getElementById("dashboard").style.display = "block";

            cargarInventario();
        } else {
            document.getElementById("loginErrorMessage").textContent = "Credenciales inválidas";
            document.getElementById("loginAlert").style.display = "block";
        }
    });

    btnMostrarFormulario.addEventListener("click", () => {
        formularioProducto.style.display = "block";
        nombreInput.value = "";
        precioInput.value = "";
        cantidadInput.value = "";
        editandoId = null;
    });

    btnGuardar.addEventListener("click", async () => {
        const producto = {
            nombre: nombreInput.value,
            precio: parseFloat(precioInput.value),
            cantidad: parseInt(cantidadInput.value)
        };

        const url = editandoId ? `/api/inventario/${editandoId}` : "/api/inventario";
        const method = editandoId ? "PUT" : "POST";

        const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(producto)
        });

        if (response.ok) {
            cargarInventario();
            formularioProducto.style.display = "none";
        } else {
            alert("Error al guardar producto");
        }
    });

    async function cargarInventario() {
        const response = await fetch("/api/inventario");
        const productos = await response.json();

        cuerpoTabla.innerHTML = "";

        productos.forEach(p => {
            const fila = document.createElement("tr");
            fila.innerHTML = `
                <td>${p.id}</td>
                <td>${p.nombre}</td>
                <td>${p.tipo || ''}</td>
                <td>${p.cantidad}</td>
                <td>${p.precio}</td>
                <td>${p.valorTotal || ''}</td>
                <td>
                    <button onclick="editarProducto(${p.id}, '${p.nombre}', ${p.precio}, ${p.cantidad})">Editar</button>
                    <button onclick="eliminarProducto(${p.id})">Eliminar</button>
                </td>
            `;
            cuerpoTabla.appendChild(fila);
        });
    }

    window.editarProducto = (id, nombre, precio, cantidad) => {
        editandoId = id;
        nombreInput.value = nombre;
        precioInput.value = precio;
        cantidadInput.value = cantidad;
        formularioProducto.style.display = "block";
    };

    window.eliminarProducto = async (id) => {
        const confirmar = confirm("¿Eliminar este producto?");
        if (confirmar) {
            await fetch(`/api/inventario/${id}`, { method: "DELETE" });
            cargarInventario();
        }
    };

    refreshBtn.addEventListener("click", cargarInventario);

    editBtn.addEventListener("click", () => {
        alert("Selecciona un producto para editar haciendo clic en el botón de la fila.");
    });

    deleteBtn.addEventListener("click", () => {
        alert("Selecciona un producto para eliminar haciendo clic en el botón de la fila.");
    });

});
