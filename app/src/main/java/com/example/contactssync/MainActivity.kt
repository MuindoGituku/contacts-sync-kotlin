package com.example.contactssync

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.contactssync.ui.theme.ContactsSyncTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContactsManagerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactsManagerApp() {
    val context = LocalContext.current
    var contacts by remember { mutableStateOf(listOf<Contact>()) }
    val scrollState = rememberScrollState()
    var contactName by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    val contactsPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.READ_CONTACTS)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
        //.verticalScroll(scrollState)
    ) {
        OutlinedTextField(
            value = contactName,
            onValueChange = { contactName = it },
            singleLine = true,
            textStyle = TextStyle(fontWeight = FontWeight.W300, fontSize = 14.sp),
            label = { Text("Contact Name") },
            supportingText = { Text("Enter the full names of the contact person") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 7.dp)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            singleLine = true,
            textStyle = TextStyle(fontWeight = FontWeight.W300, fontSize = 14.sp),
            label = { Text("Phone Number") },
            supportingText = { Text("Enter the phone number of the contact person") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 7.dp)
                .fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    if ((ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.WRITE_CONTACTS
                        ) == PackageManager.PERMISSION_GRANTED && (contactName.isNotEmpty() && contactNumber.isNotEmpty()))
                    ) {
                        addContact(context, contactName, contactNumber)
                    } else {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.WRITE_CONTACTS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            contactsPermissionState.launchPermissionRequest()
                        }
                        if (contactName.isEmpty() || contactNumber.isEmpty()){
                            Toast.makeText(context, "All fields are required!!", Toast.LENGTH_SHORT).show()
                        }

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(255, 193, 7, 255)),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .padding(start = 10.dp)
                    .background(
                        color = Color(255, 193, 7, 255),
                        shape = RoundedCornerShape(corner = CornerSize(20))
                    )
                    .border(
                        width = 2.dp,
                        color = Color(255, 235, 59, 255),
                        shape = RoundedCornerShape(corner = CornerSize(20))
                    ),
            ) {
                Text(
                    text = "Add Contact",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(255, 193, 7, 255)),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .padding(start = 10.dp)
                    .background(
                        color = Color(255, 193, 7, 255),
                        shape = RoundedCornerShape(corner = CornerSize(20))
                    )
                    .border(
                        width = 2.dp,
                        color = Color(255, 235, 59, 255),
                        shape = RoundedCornerShape(corner = CornerSize(20))
                    ),
                onClick = {
                    if (contactsPermissionState.status.isGranted) {
                        contacts = fetchContacts(context)
                    } else {
                        contactsPermissionState.launchPermissionRequest()
                    }
                },
            ) {
                Text(
                    text = "Load Contacts",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        LazyColumn {
            items(contacts) { contact ->
                ListItem(
                    leadingContent = {
                        Image(
                            painter = painterResource(R.drawable.user),
                            contentDescription = "Contact Icon",
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    headlineContent = { Text(contact.name) },
                    supportingContent = { Text(contact.phoneNumber) }
                )
                Divider()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ContactsSyncTheme {
        ContactsManagerApp()
    }
}