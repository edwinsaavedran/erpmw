document.addEventListener('DOMContentLoaded', function () {
    const detallesContainer = document.getElementById('detalles-container');
    const addItemBtn = document.getElementById('btn-agregar-detalle');
    const itemTemplate = document.getElementById('detalle-template');
    const ordenTotalDisplay = document.getElementById('orden-total-display');
    // const ordenTotalHiddenInput = document.getElementById('totalCalculadoHidden'); // Si decides enviar el total

    let detalleIndex = detallesContainer.querySelectorAll('.item-row:not(#detalle-template)').length;

    // Función para actualizar el precio y subtotal de una fila
    function actualizarFila(row) {
        const productoSelect = row.querySelector('.item-producto-proveedor');
        const cantidadInput = row.querySelector('.item-cantidad');
        const precioUnitarioDisplay = row.querySelector('.item-precio-unitario');
        const subtotalDisplay = row.querySelector('.item-subtotal');

        let precioUnitario = 0;
        if (productoSelect && productoSelect.value) {
            const selectedOption = productoSelect.options[productoSelect.selectedIndex];
            precioUnitario = parseFloat(selectedOption.getAttribute('data-precio') || '0');
        }

        const cantidad = parseInt(cantidadInput.value || '0');

        if (precioUnitarioDisplay) {
            precioUnitarioDisplay.value = precioUnitario.toFixed(2);
        }

        const subtotal = (cantidad >= 1 && precioUnitario > 0) ? cantidad * precioUnitario : 0;
        if (subtotalDisplay) {
            subtotalDisplay.value = subtotal.toFixed(2);
        }
        actualizarTotalGeneral();
    }

    // Función para actualizar el total general de la orden
    function actualizarTotalGeneral() {
        let totalGeneral = 0;
        detallesContainer.querySelectorAll('.item-row:not(#detalle-template)').forEach(row => {
            const subtotalDisplay = row.querySelector('.item-subtotal');
            if (subtotalDisplay && subtotalDisplay.value) {
                totalGeneral += parseFloat(subtotalDisplay.value);
            }
        });
        if (ordenTotalDisplay) {
            ordenTotalDisplay.textContent = totalGeneral.toFixed(2);
        }
        // if (ordenTotalHiddenInput) {
        //     ordenTotalHiddenInput.value = totalGeneral.toFixed(2);
        // }
    }

    // Función para configurar los event listeners en una fila de detalle
    function configurarFila(row) {
        const productoSelect = row.querySelector('.item-producto-proveedor');
        const cantidadInput = row.querySelector('.item-cantidad');
        const removeBtn = row.querySelector('.remove-item-btn');

        if (productoSelect) {
            productoSelect.addEventListener('change', function() {
                actualizarFila(row);
                validarSeleccionProducto(this, row); // Validar selección
            });
        }
        if (cantidadInput) {
            cantidadInput.addEventListener('input', function() {
                actualizarFila(row);
                validarCantidad(this, row); // Validar cantidad
            });
        }
        if (removeBtn) {
            removeBtn.addEventListener('click', function () {
                row.remove();
                actualizarTotalGeneral();
                renumerarIndices(); // Importante re-indexar después de eliminar
            });
        }
        // Inicializar/validar fila al cargar (si viene con datos)
        if (productoSelect) validarSeleccionProducto(productoSelect, row);
        if (cantidadInput) validarCantidad(cantidadInput, row);
    }
    
    // Validaciones simples del lado del cliente
    function validarSeleccionProducto(selectElement, row) {
        const feedbackElement = row.querySelector('.item-producto-proveedor + .invalid-feedback') || 
                               row.querySelector('select[data-name-template*="idProductoProveedor"] + .invalid-feedback');
        if (selectElement.value === "") {
            selectElement.classList.add('is-invalid');
            if(feedbackElement) feedbackElement.style.display = 'block';
        } else {
            selectElement.classList.remove('is-invalid');
             if(feedbackElement) feedbackElement.style.display = 'none';
        }
    }

    function validarCantidad(inputElement, row) {
        const feedbackElement = row.querySelector('.item-cantidad + .invalid-feedback') ||
                                row.querySelector('input[data-name-template*="cantidad"] + .invalid-feedback');
        if (parseInt(inputElement.value) <= 0 || inputElement.value === "") {
            inputElement.classList.add('is-invalid');
            if(feedbackElement) feedbackElement.style.display = 'block';
        } else {
            inputElement.classList.remove('is-invalid');
            if(feedbackElement) feedbackElement.style.display = 'none';
        }
    }


    // Función para añadir una nueva fila de detalle
    function agregarNuevaFilaDetalle() {
        const nuevaFila = itemTemplate.cloneNode(true);
        nuevaFila.removeAttribute('id');
        nuevaFila.style.display = 'flex'; // O el display que corresponda a .item-row

        // Configurar nombres de campos para el binding con Spring MVC (detalles[INDEX].campo)
        nuevaFila.querySelectorAll('[data-name-template]').forEach(el => {
            el.name = el.getAttribute('data-name-template').replace('#INDEX#', detalleIndex);
            el.id = el.name.replace(/[\[\].]/g, '-'); // Crear un ID único basado en el nombre
        });
        
        // Limpiar el valor del select clonado y otros campos si es necesario
        const productoSelectClonado = nuevaFila.querySelector('.item-producto-proveedor');
        if(productoSelectClonado) productoSelectClonado.value = "";
        const cantidadInputClonado = nuevaFila.querySelector('.item-cantidad');
        if(cantidadInputClonado) cantidadInputClonado.value = "1";
        const precioUnitarioClonado = nuevaFila.querySelector('.item-precio-unitario');
        if(precioUnitarioClonado) precioUnitarioClonado.value = "";
        const subtotalClonado = nuevaFila.querySelector('.item-subtotal');
        if(subtotalClonado) subtotalClonado.value = "";


        configurarFila(nuevaFila);
        detallesContainer.appendChild(nuevaFila);
        detalleIndex++;
        actualizarTotalGeneral();
    }

    // Función para re-numerar los índices de los campos de detalle (importante si se eliminan filas intermedias)
    function renumerarIndices() {
        let currentIndex = 0;
        detallesContainer.querySelectorAll('.item-row:not(#detalle-template)').forEach(row => {
            row.querySelectorAll('[name^="detalles["]').forEach(el => {
                el.name = el.name.replace(/detalles\[\d+\]/, `detalles[${currentIndex}]`);
                el.id = el.name.replace(/[\[\].]/g, '-'); 
            });
            // Actualizar también los IDs de los labels si es necesario
             row.querySelectorAll('label[for^="detalles-"]').forEach(label => {
                const oldFor = label.getAttribute('for');
                if (oldFor) {
                    label.setAttribute('for', oldFor.replace(/detalles-\d+-/, `detalles-${currentIndex}-`));
                }
            });
            currentIndex++;
        });
        detalleIndex = currentIndex; // Actualizar el contador global de índices
    }


    // Configurar filas existentes (ej. al volver de un error de validación)
    detallesContainer.querySelectorAll('.item-row:not(#detalle-template)').forEach(row => {
        configurarFila(row);
        actualizarFila(row); // Para calcular subtotales de filas existentes
    });
    actualizarTotalGeneral(); // Calcular total general inicial


    // Event listener para el botón "Agregar Ítem"
    if (addItemBtn) {
        addItemBtn.addEventListener('click', agregarNuevaFilaDetalle);
    }
    
    // Validación global del formulario antes de enviar (opcional, ya que hay validación de backend)
    const form = document.getElementById('ordenCompraForm');
    if (form) {
        form.addEventListener('submit', function(event) {
            let formValido = true;
            let primerElementoInvalido = null;

            // Validar que haya al menos un detalle
            if (detallesContainer.querySelectorAll('.item-row:not(#detalle-template)').length === 0) {
                alert('Debe agregar al menos un ítem a la orden de compra.');
                formValido = false;
                // Aquí podrías mostrar un error más integrado en la UI
            }

            // Validar cada fila de detalle
            detallesContainer.querySelectorAll('.item-row:not(#detalle-template)').forEach(row => {
                const productoSelect = row.querySelector('.item-producto-proveedor');
                const cantidadInput = row.querySelector('.item-cantidad');
                
                validarSeleccionProducto(productoSelect, row);
                validarCantidad(cantidadInput, row);

                if (productoSelect.value === "") {
                    formValido = false;
                    if (!primerElementoInvalido) primerElementoInvalido = productoSelect;
                }
                if (parseInt(cantidadInput.value) <= 0 || cantidadInput.value === "") {
                     formValido = false;
                    if (!primerElementoInvalido) primerElementoInvalido = cantidadInput;
                }
            });

            if (!formValido) {
                event.preventDefault(); // Detener el envío
                alert('Por favor, corrija los errores en el formulario antes de guardar.');
                if (primerElementoInvalido) {
                    primerElementoInvalido.focus();
                }
            }
        });
    }
});