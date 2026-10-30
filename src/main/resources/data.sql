INSERT INTO team_member (nombre, cargo, email, activo) VALUES
('Ana Morales', 'QA Lead', 'ana.morales@smartnotes.com', true),
('Pedro Ruiz', 'Release Manager', 'pedro.ruiz@smartnotes.com', true),
('Lucía Solís', 'Supervisora de calidad', 'lucia.solis@smartnotes.com', true);

INSERT INTO plantilla (nombre, descripcion, texto_base, activo) VALUES
('Plantilla de auditoría', 'Genera una nota de verificación y corrección para pruebas de calidad.',
 'Nota de corrección para {{plantilla}}.\nMiembro: {{teamMember}} ({{cargo}}).\nPeriodo: {{fechaInicio}} al {{fechaFin}}.\nDescripción: {{descripcion}}\nCorrección: {{correccion}}\nEvidencias: {{evidencias}}',
 true),
('Plantilla release', 'Nota de aprobación de versiones y despliegues.',
 'Release review para {{teamMember}}.\nCargo: {{cargo}}.\nFecha: {{fechaInicio}} a {{fechaFin}}.\nObservación principal: {{descripcion}}\nAcción: {{correccion}}\nPruebas / evidencias: {{evidencias}}',
 true);

INSERT INTO variable (clave, nombre, tipo, orden, plantilla_id) VALUES
('teamMember', 'Miembro', 'texto', 1, 1),
('cargo', 'Cargo', 'texto', 2, 1),
('fechaInicio', 'Fecha inicio', 'date', 3, 1),
('fechaFin', 'Fecha fin', 'date', 4, 1),
('descripcion', 'Descripción', 'textarea', 5, 1),
('correccion', 'Corrección', 'textarea', 6, 1),
('evidencias', 'Evidencias', 'textarea', 7, 1),
('teamMember', 'Miembro', 'texto', 1, 2),
('cargo', 'Cargo', 'texto', 2, 2),
('descripcion', 'Descripción', 'textarea', 3, 2),
('correccion', 'Corrección', 'textarea', 4, 2),
('evidencias', 'Evidencias', 'textarea', 5, 2);

INSERT INTO nota_correccion (plantilla_id, team_member_id, fecha_inicio, fecha_fin, descripcion, correccion, contenido_generado, evidencias, estado, fecha_creacion, fecha_actualizacion) VALUES
(1, 1, '2026-10-01', '2026-10-05', 'Se encontraron errores en la validación del módulo de notas.', 'Se ajustó la validación y se verificó la generación de la vista previa.', 'Nota de corrección para Plantilla de auditoría.\nMiembro: Ana Morales (QA Lead).\nPeriodo: 01/10/2026 al 05/10/2026.\nDescripción: Se encontraron errores en la validación del módulo de notas.\nCorrección: Se ajustó la validación y se verificó la generación de la vista previa.\nEvidencias: Captura 1; Captura 2', 'Captura 1; Captura 2', 'GUARDADA', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '2026-10-08', '2026-10-10', 'La versión de despliegue requería validación del entorno final.', 'Se validó la release con resultados exitosos y se cerró la aprobación.', 'Release review para Pedro Ruiz.\nCargo: Release Manager.\nFecha: 08/10/2026 a 10/10/2026.\nObservación principal: La versión de despliegue requería validación del entorno final.\nAcción: Se validó la release con resultados exitosos y se cerró la aprobación.\nPruebas / evidencias: Informe de despliegue', 'Informe de despliegue', 'BORRADOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
