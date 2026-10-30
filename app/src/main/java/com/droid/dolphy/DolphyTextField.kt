package com.droid.dolphy

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun DolphyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val liquid = LocalLiquidGlassEnabled.current
    val colors = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(LiquidRadius.Card),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            disabledTextColor = colors.onSurface.copy(alpha = 0.38f),
            errorTextColor = colors.onSurface,
            focusedContainerColor = colors.surfaceContainerHigh.copy(alpha = if (liquid) 0.76f else 1f),
            unfocusedContainerColor = colors.surfaceContainer.copy(alpha = if (liquid) 0.64f else 1f),
            disabledContainerColor = colors.surfaceContainer.copy(alpha = 0.36f),
            errorContainerColor = colors.surfaceContainerHigh.copy(alpha = if (liquid) 0.76f else 1f),
            cursorColor = colors.primary,
            errorCursorColor = colors.error,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outlineVariant,
            disabledBorderColor = colors.outlineVariant.copy(alpha = 0.38f),
            errorBorderColor = colors.error,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurfaceVariant,
            disabledLabelColor = colors.onSurfaceVariant.copy(alpha = 0.38f),
            errorLabelColor = colors.error,
            focusedPlaceholderColor = colors.onSurfaceVariant.copy(alpha = 0.72f),
            unfocusedPlaceholderColor = colors.onSurfaceVariant.copy(alpha = 0.72f),
            focusedSupportingTextColor = colors.onSurfaceVariant,
            unfocusedSupportingTextColor = colors.onSurfaceVariant,
            errorSupportingTextColor = colors.error,
        ),
    )
}
