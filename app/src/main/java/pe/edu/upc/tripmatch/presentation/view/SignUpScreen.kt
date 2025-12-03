package pe.edu.upc.tripmatch.presentation.view

import pe.edu.upc.tripmatch.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthEvent
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

private val TealDark = Color(0xFF318C8B)
private val TealLight = Color(0xFFD9F2EF)
private val TextSecondary = Color(0xFF58636A)

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateTo: (AuthFlowScreen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var acceptedTerms by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    val canSubmit = remember(uiState, acceptedTerms) {
        uiState.name.isNotBlank() &&
                uiState.email.isNotBlank() &&
                uiState.number.isNotBlank() &&
                uiState.password.length >= 6 &&
                uiState.password == uiState.confirmPassword &&
                acceptedTerms
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            if (event == AuthEvent.SignUpSuccess) {
                Toast.makeText(context, "Cuenta creada con éxito. Inicia sesión.", Toast.LENGTH_LONG).show()
                onNavigateTo(AuthFlowScreen.Login)
                viewModel.clearEvent()
            }
        }
    }
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Column {
                    Text(
                        text = "Términos y condiciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TealDark
                    )
                    Text(
                        text = "Última actualización: 2 de diciembre de 2025",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 8.dp),
                        color = TealLight
                    )
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                    TermsContent()
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showTermsDialog = false
                        acceptedTerms = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entiendo y acepto", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showTermsDialog = false },
                    border = BorderStroke(1.dp, TealDark),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealDark)
                ) {
                    Text("Cerrar")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tripmatch_logo),
                    contentDescription = "TripMatch",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(bottom = 6.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "TripMatch",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Crea tu cuenta",
                    fontSize = 15.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            LoginSignUpToggle(
                selected = 1,
                onSelectLogin = { onNavigateTo(AuthFlowScreen.Login) },
                onSelectSignUp = { /* signup */ }
            )

            Spacer(Modifier.height(14.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    uiState.errorMessage?.let {
                        Text(
                            it,
                            color = Color(0xFFD11A2A),
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    AuthInputField(
                        value = uiState.name,
                        onValueChange = { viewModel.setName(it) },
                        label = "Nombre completo",
                        icon = Icons.Default.Person
                    )

                    AuthInputField(
                        value = uiState.email,
                        onValueChange = { viewModel.setEmail(it) },
                        label = "Correo electrónico",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    AuthInputField(
                        value = uiState.number,
                        onValueChange = { viewModel.setNumber(it) },
                        label = "Teléfono",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    AuthInputField(
                        value = uiState.password,
                        onValueChange = { viewModel.setPassword(it) },
                        label = "Contraseña (min. 6)",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    AuthInputField(
                        value = uiState.confirmPassword,
                        onValueChange = { viewModel.setConfirmPassword(it) },
                        label = "Confirmar contraseña",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Tipo de cuenta", fontWeight = FontWeight.SemiBold, color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !uiState.isAgency,
                                onClick = { viewModel.setIsAgency(false) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealDark)
                            )
                            Text("Turista", modifier = Modifier.clickable { viewModel.setIsAgency(false) }, fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.isAgency,
                                onClick = { viewModel.setIsAgency(true) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealDark)
                            )
                            Text("Agencia", modifier = Modifier.clickable { viewModel.setIsAgency(true) }, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = acceptedTerms,
                            onCheckedChange = { acceptedTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = TealDark,
                                uncheckedColor = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val annotatedText = buildAnnotatedString {
                            append("He leído y acepto los ")
                            pushStringAnnotation(tag = "TERMS", annotation = "terms")
                            withStyle(style = SpanStyle(color = TealDark, fontWeight = FontWeight.Bold)) {
                                append("Términos y Condiciones")
                            }
                            pop()
                        }

                        ClickableText(
                            text = annotatedText,
                            onClick = { offset ->
                                annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        showTermsDialog = true
                                    }
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    PrimaryButton(
                        text = "CREAR CUENTA",
                        onClick = { viewModel.onSignUp() },
                        enabled = canSubmit && !uiState.isLoading,
                        isLoading = uiState.isLoading
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("¿Ya tienes cuenta? ", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            "Inicia sesión",
                            color = TealDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateTo(AuthFlowScreen.Login) },
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun LoginSignUpToggle(
    selected: Int,
    onSelectLogin: () -> Unit,
    onSelectSignUp: () -> Unit
) {
    val containerShape = CircleShape
    Row(
        modifier = Modifier
            .clip(containerShape)
            .background(TealLight)
            .padding(4.dp)
            .height(36.dp)
            .widthIn(min = 220.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(containerShape)
                .background(if (selected == 0) TealDark else Color.Transparent)
                .clickable { onSelectLogin() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Inicia Sesión",
                color = if (selected == 0) Color.White else TealDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(containerShape)
                .background(if (selected == 1) TealDark else Color.Transparent)
                .clickable { onSelectSignUp() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Regístrate",
                color = if (selected == 1) Color.White else TealDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TermsContent() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(end = 8.dp)
    ) {
        TermParagraph("Por favor, lea atentamente estos Términos y Condiciones antes de utilizar la aplicación TripMatch.")

        TermSectionTitle("1. Interpretación y Definiciones")
        TermSubtitle("Interpretación")
        TermParagraph("Las palabras con inicial mayúscula tienen significados definidos bajo las siguientes condiciones. Estas definiciones se aplicarán independientemente de si aparecen en singular o en plural.")

        TermSubtitle("Definiciones")
        TermParagraph("Para los propósitos de estos Términos y Condiciones:")
        BulletPoint("Aplicación", "significa el software TripMatch proporcionado por la Compañía y descargado en cualquier dispositivo electrónico.")
        BulletPoint("Tienda de Aplicaciones", "significa Apple App Store, Google Play Store u otras plataformas de distribución digital donde la Aplicación esté disponible.")
        BulletPoint("Compañía", "(“Nosotros”, “Nos” o “Nuestro”) se refiere a Xplora, ubicada en Lima, San Miguel, Perú.")
        BulletPoint("Servicio", "se refiere a la Aplicación.")
        BulletPoint("Usuario", "(“Usted”) significa cualquier persona física o jurídica que utilice el Servicio.")

        TermParagraph("TripMatch tiene dos tipos de Usuarios:")
        TermListUi(listOf("Usuarios Turistas – Personas que buscan experiencias de viaje.", "Usuarios Agencia – Agencias de viajes registradas que ofrecen servicios, tours o experiencias."))

        BulletPoint("Cuenta", "significa el perfil único creado para acceder al Servicio.")
        BulletPoint("Dispositivo", "significa cualquier dispositivo capaz de acceder al Servicio.")
        BulletPoint("Contenido", "significa cualquier texto, imagen, dato, reseña, listado, comentario u otro medio enviado al Servicio.")

        TermSectionTitle("2. Reconocimiento")
        TermParagraph("Estos Términos y Condiciones rigen el uso del Servicio y constituyen un acuerdo vinculante entre Usted y la Compañía.")
        TermParagraph("Su acceso al Servicio está condicionado a su aceptación de estos Términos. Si no está de acuerdo con alguna parte, no podrá acceder al Servicio.")
        TermParagraph("Usted declara tener al menos 18 años. El Servicio no está destinado a personas menores de 18 años.")
        TermParagraph("El uso del Servicio también está sujeto a nuestra Política de Privacidad, que describe cómo recopilamos y utilizamos los datos personales.")

        TermSectionTitle("3. Creación de Cuenta y Responsabilidades del Usuario")
        TermSubtitle("3.1 Tipos de Cuenta")
        TermParagraph("TripMatch permite dos tipos de cuentas:")
        BulletPoint("Cuentas de Turista", "Creadas por personas que buscan experiencias de viaje, tours o paquetes de aventura.")
        BulletPoint("Cuentas de Agencia", "Creadas por agencias de turismo legalmente registradas para ofrecer servicios a través de TripMatch.")
        TermParagraph("Las agencias deben proporcionar: Nombre legal del negocio, Correo electrónico, Número de teléfono, Prueba de operación o registro.")
        TermParagraph("La Compañía puede solicitar documentación adicional para verificar la autenticidad de la agencia.")

        TermSubtitle("3.2 Seguridad de la Cuenta")
        TermParagraph("Usted acepta:")
        TermListUi(listOf(
            "Proporcionar información precisa, completa y actualizada.",
            "Mantener la confidencialidad de sus credenciales.",
            "Notificar a la Compañía inmediatamente sobre cualquier acceso no autorizado."
        ))
        TermParagraph("La Compañía no se hace responsable de los daños derivados del incumplimiento de las obligaciones de seguridad.")

        TermSubtitle("3.3 Inicio de Sesión con Redes Sociales")
        TermParagraph("Los usuarios pueden iniciar sesión utilizando Servicios de Redes Sociales de Terceros (ej. Google). Al hacerlo, Usted autoriza a la Compañía a acceder a los datos básicos de perfil permitidos por el proveedor.")

        TermSectionTitle("4. Uso del Servicio")
        TermParagraph("Usted acepta NO utilizar el Servicio para:")
        TermListUi(listOf(
            "Publicar información falsa, engañosa o fraudulenta.",
            "Hacerse pasar por otra persona o agencia.",
            "Subir contenido dañino, ilegal u ofensivo.",
            "Interferir con el funcionamiento del Servicio (ej. malware, scraping, hacking).",
            "Publicar materiales con derechos de autor sin permiso."
        ))
        TermParagraph("La Compañía puede suspender o cancelar cualquier cuenta que viole estas reglas.")

        TermSectionTitle("5. Listados de Agencias y Responsabilidades")
        TermParagraph("Las agencias que utilizan TripMatch aceptan:")
        TermListUi(listOf(
            "Proporcionar descripciones precisas de tours, precios, horarios y políticas.",
            "Mantener la disponibilidad y la información actualizadas.",
            "Responder a las consultas de los turistas dentro de plazos razonables.",
            "Cumplir con todas las leyes regionales de turismo y regulaciones de seguridad.",
            "Ser los únicos responsables de la calidad y ejecución de los servicios ofrecidos."
        ))
        TermParagraph("TripMatch actúa como una plataforma, no como una agencia de viajes. No operamos tours, no garantizamos el desempeño de la agencia ni asumimos responsabilidad por disputas entre Usuarios.")

        TermSectionTitle("6. Reservas, Pagos y Cancelaciones (Si Aplica)")
        TermParagraph("Si TripMatch habilita reservas o pagos en el futuro:")
        TermListUi(listOf(
            "La Compañía puede actuar como intermediario para procesar reservas.",
            "Las agencias deben establecer claramente las políticas de cancelación.",
            "Los turistas deben revisar y aceptar las políticas de la Agencia antes de reservar."
        ))
        TermParagraph("La Compañía no es responsable de los cambios realizados por las agencias, tarifas de cancelación o disputas de reembolso.")
        TermParagraph("Actualmente, TripMatch puede operar como una plataforma de emparejamiento, y cualquier pago o acuerdo fuera de la Aplicación es responsabilidad exclusiva de las partes involucradas.")

        TermSectionTitle("7. Contenido Generado por el Usuario")
        TermParagraph("Los usuarios pueden enviar reseñas, fotos o comentarios. Al hacerlo, Usted otorga a la Compañía una licencia no exclusiva, mundial, libre de regalías y transferible para usar, mostrar, reproducir y distribuir dicho contenido con fines de mejorar o promocionar el Servicio.")
        TermParagraph("Usted conserva la propiedad de su contenido. La Compañía puede eliminar contenido a su entera discreción si viola estos Términos.")

        TermSectionTitle("8. Propiedad Intelectual")
        TermParagraph("La Aplicación, incluidos logotipos, gráficos, software y marcas comerciales, es propiedad de la Compañía y está protegida por leyes nacionales e internacionales.")
        TermParagraph("Usted no puede copiar, modificar, distribuir, realizar ingeniería inversa, vender o sublicenciar ninguna parte de la Aplicación sin permiso por escrito.")

        TermSectionTitle("9. Enlaces de Terceros")
        TermParagraph("TripMatch puede contener enlaces a sitios web de terceros. La Compañía no es responsable de su contenido, políticas o fiabilidad.")
        TermParagraph("Le recomendamos revisar los términos y políticas de privacidad de terceros.")

        TermSectionTitle("10. Terminación")
        TermParagraph("Podemos terminar o suspender su acceso inmediatamente si Usted viola estos Términos, participa en actividades fraudulentas o dañinas, o proporciona información falsa.")
        TermParagraph("Al momento de la terminación, Usted debe dejar de usar el Servicio inmediatamente.")

        TermSectionTitle("11. Limitación de Responsabilidad")
        TermParagraph("En la medida máxima permitida por la ley, la Compañía no será responsable de:")
        TermListUi(listOf("Pérdida de beneficios", "Pérdida de datos", "Lesiones personales", "Mala conducta de la agencia", "Cancelaciones o fallas en el servicio por parte de las Agencias", "Daños resultantes del mal uso de la plataforma."))
        TermParagraph("Nuestra responsabilidad total no excederá la cantidad pagada por Usted a través del Servicio, o 100 USD si no se realizaron compras.")

        TermSectionTitle("12. Descargo de Responsabilidad “TAL CUAL”")
        TermParagraph("El Servicio se proporciona “TAL CUAL” y “SEGÚN DISPONIBILIDAD”, sin garantías de ningún tipo. No garantizamos que el Servicio esté libre de errores, funcione sin interrupciones, que los listados de agencias sean precisos o que el contenido del Usuario sea veraz.")

        TermSectionTitle("13. Ley Aplicable")
        TermParagraph("Estos Términos se rigen por las leyes de Perú, sin tener en cuenta los principios de conflicto de leyes.")

        TermSectionTitle("14. Resolución de Disputas")
        TermParagraph("Usted acepta intentar resolver disputas de manera informal contactando primero a la Compañía. Si no se resuelven, las disputas se manejarán bajo la jurisdicción de los tribunales peruanos.")

        TermSectionTitle("15. Cambios a Estos Términos")
        TermParagraph("Podemos actualizar estos Términos en cualquier momento. Si los cambios son significativos, proporcionaremos un aviso dentro de la Aplicación. Su uso continuo del Servicio constituye la aceptación de los Términos revisados.")

        TermSectionTitle("16. Contáctenos")
        TermParagraph("Si tiene preguntas sobre estos Términos, puede contactarnos en:")
        Text(
            text = "Email: u20231c505@upc.edu.pe",
            fontWeight = FontWeight.Bold,
            color = TealDark,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun TermSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color.Black,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun TermSubtitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun TermParagraph(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = TextSecondary,
        lineHeight = 18.sp,
        textAlign = TextAlign.Justify,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun BulletPoint(title: String, description: String) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextSecondary)) {
            append("• $title: ")
        }
        append(description)
    }
    Text(
        text = text,
        fontSize = 13.sp,
        color = TextSecondary,
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
    )
}

@Composable
fun TermListUi(items: List<String>) {
    Column(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)) {
        items.forEach { item ->
            Row(verticalAlignment = Alignment.Top) {
                Text("•", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = item,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}