DESIGN.md

LIQUID GLASS — ANDROID DESIGN SYSTEM

Premium UI/UX · Fluid Motion · Modern Native Experience

Design Direction: Apple iOS Liquid Glass × Modern Android
Platform: Android APK
Primary Experience: Mobile-first
Design Language: Glassmorphism, Depth, Fluidity, Minimalism
Design Priority: Visual Quality, Smooth Interaction, Performance, Accessibility

---

01. DESIGN PHILOSOPHY

Create a sophisticated Android application inspired by Apple's Liquid Glass visual language.

The application should feel like a premium, carefully designed product rather than a generic Android application.

The design must communicate:

- Premium and modern aesthetics.
- Fluid and natural interactions.
- Elegant glass materials.
- Sophisticated visual hierarchy.
- Smooth transitions between screens.
- Responsive and intuitive controls.
- Lightweight and efficient performance.

The experience should feel alive, but never distracting.

Core Principles

1. Glass should feel like a real material, not just transparent rectangles.
2. Motion should feel organic, not robotic.
3. Components should respond naturally to user interactions.
4. Every screen should maintain visual consistency.
5. Animations must prioritize smoothness over complexity.
6. Visual effects must never compromise usability.
7. Performance must remain stable on mid-range Android devices.

IMPORTANT: Do not blindly copy iOS layouts. Adapt the Liquid Glass philosophy into a native Android experience.

---

02. VISUAL IDENTITY

Liquid Glass Material

Liquid Glass is the primary visual language.

Glass surfaces should create a sense of depth through layered translucency, background blur, subtle reflections, and light interaction.

Glass Characteristics

- Semi-transparent surfaces.
- Background blur.
- Subtle light reflections.
- Delicate borders.
- Soft ambient shadows.
- Layered depth.
- Natural highlights.
- Context-aware opacity.

Glass Variants

A. Primary Glass

Used for major navigation and floating panels.

background: rgba(255, 255, 255, 0.14);
backdrop-filter: blur(28px) saturate(180%);
border: 1px solid rgba(255,255,255,0.20);

B. Secondary Glass

Used for cards, buttons, and secondary controls.

background: rgba(255, 255, 255, 0.09);
backdrop-filter: blur(18px);
border: 1px solid rgba(255,255,255,0.12);

C. Dark Glass

Used for dark backgrounds and floating overlays.

background: rgba(20, 20, 24, 0.58);
backdrop-filter: blur(26px) saturate(160%);
border: 1px solid rgba(255,255,255,0.10);

These CSS snippets describe the visual target. For native Android, implement equivalent effects using supported native blur, translucent surfaces, gradients, and compositing.

Important Rules

- Never apply glass to every component.
- Maintain readable text contrast.
- Avoid excessive transparency.
- Avoid exaggerated glow effects.
- Use realistic depth and subtle reflections.
- Glass must complement the background rather than obscure it.

---

03. COLOR SYSTEM

Use a sophisticated neutral color palette.

The design should not look overly colorful, childish, or visually noisy.

Base Palette

Background       #101114
Surface          #191B20
Elevated Surface #22252B

Primary Text     #F5F5F7
Secondary Text   #A5A7AF
Muted Text       #777B85

Glass Light      rgba(255,255,255,0.12)
Glass Border     rgba(255,255,255,0.16)

Accent           #A78BFA
Accent Secondary #8B9CF6

Success          #65C995
Warning          #E8B66D
Error            #EF7777

Color Guidelines

- Use dark AMOLED-friendly backgrounds as the default visual direction.
- Use accent colors sparingly.
- Avoid excessive gradients.
- Avoid neon overload.
- Maintain contrast between foreground and background.
- Status colors must remain consistent throughout the application.

The color system should be configurable without redesigning individual screens.

---

04. TYPOGRAPHY

Typography must feel clean, modern, and premium.

Recommended Fonts

Primary:

- Inter
- SF Pro-inspired system typography
- Roboto

Use native Android system fonts where appropriate.

Typography Hierarchy

Element| Style
Main heading| 28–34sp, Semibold
Section heading| 20–24sp, Semibold
Card heading| 16–18sp, Medium
Body text| 14–16sp, Regular
Secondary text| 12–14sp, Regular
Small labels| 11–12sp, Medium

Typography Rules

- Avoid unnecessarily bold text.
- Maintain consistent font weights.
- Use comfortable line spacing.
- Avoid excessive uppercase labels.
- Maintain proper text scaling.
- Support Android font-size accessibility settings.

---

05. LAYOUT & SPACING

Use a consistent 4dp-based spacing system.

4dp   = Micro spacing
8dp   = Tight spacing
12dp  = Small spacing
16dp  = Standard spacing
20dp  = Medium spacing
24dp  = Large spacing
32dp  = Section spacing
40dp  = Major spacing
48dp  = Layout separation

Corner Radius

8dp   = Small controls
12dp  = Standard components
16dp  = Cards
20dp  = Large cards
24dp  = Floating panels
28dp  = Major glass surfaces
999dp = Capsule buttons

Do not use maximum rounding everywhere.

Different component types should have appropriate corner radii.

Layout Principles

- Consistent horizontal padding.
- Proper content alignment.
- Comfortable touch spacing.
- Clear separation between sections.
- No unnecessary visual containers.
- Respect system status bar and navigation bar insets.
- Support different screen sizes and aspect ratios.

---

06. COMPONENT DESIGN SYSTEM

Every screen must use a shared component system.

6.1 Glass Navigation Bar

Create a floating navigation bar inspired by modern iOS interfaces.

Characteristics:

- Floating appearance.
- Translucent glass material.
- Background blur.
- Subtle border.
- Smooth active indicator.
- Natural icon transitions.
- Consistent touch targets.

Interactions:

- Active item transitions smoothly.
- Icons respond subtly to selection.
- Press feedback feels tactile.
- Navigation state remains visually clear.

Avoid excessive bouncing or unnecessary icon animations.

6.2 Glass Cards

Cards should feel layered and lightweight.

Features:

- Translucent background.
- Subtle border.
- Soft shadow.
- Optional light reflection.
- Smooth hover or press feedback where applicable.

Cards should not all have identical visual treatment.

Use hierarchy to distinguish important information.

6.3 Glass Buttons

Create three button variants:

Primary Button

- High visual contrast.
- Clear action hierarchy.
- Subtle accent treatment.

Secondary Button

- Translucent background.
- Delicate border.
- Less visual prominence.

Icon Button

- Minimal glass surface.
- Centered icon.
- Comfortable touch target.

Interaction states:

- Default.
- Pressed.
- Focused.
- Disabled.
- Loading.
- Success or error where relevant.

6.4 Input Fields

Input fields must feel integrated with the glass design.

Requirements:

- Subtle translucent surface.
- Clear placeholder.
- Smooth focus transition.
- Visible focus indicator.
- Proper keyboard behavior.
- Clear validation feedback.
- Comfortable input spacing.

Avoid overly decorative input fields.

6.5 Dialogs & Bottom Sheets

Use layered glass panels.

Requirements:

- Smooth entrance and dismissal.
- Rounded upper corners for bottom sheets.
- Background dimming.
- Optional background blur.
- Natural drag interaction.
- Clear action hierarchy.

Dialogs should never feel abrupt or intrusive.

---

07. MOTION DESIGN SYSTEM

THE MOST IMPORTANT SECTION

Animations are a core part of this design system.

The application must feel fluid, responsive, and alive.

NO STIFF ANIMATIONS. NO ROBOTIC TRANSITIONS. NO ABRUPT MOVEMENT.

Every interaction must feel intentionally designed.

7.1 Motion Philosophy

Use natural movement inspired by physical interactions.

Animations should have:

- Smooth acceleration.
- Natural deceleration.
- Fluid transitions.
- Subtle overshoot when appropriate.
- Context-aware movement.
- Consistent timing.
- Responsive interruption.

Avoid:

- Constant linear animations.
- Excessive bounce.
- Unnecessary rotation.
- Overly dramatic zoom effects.
- Long delays.
- Animations that block user interaction.

7.2 Animation Timing

Recommended timing ranges:

Interaction| Duration
Button feedback| 100–160ms
Icon transition| 150–220ms
Small state change| 160–240ms
Card transition| 220–320ms
Screen transition| 280–420ms
Bottom sheet| 300–450ms
Complex entrance| 350–550ms

These are starting points, not mandatory fixed durations.

Animations must adapt to the complexity of the transition.

7.3 Spring Physics

Prefer spring-based motion over basic linear interpolation.

Recommended conceptual configuration:

Stiffness: Medium
Damping: Medium-High
Mass: Natural
Overshoot: Subtle

The movement should feel responsive without excessive bouncing.

For Kotlin Jetpack Compose, use:

- spring()
- animateFloatAsState()
- animateDpAsState()
- AnimatedVisibility
- AnimatedContent
- updateTransition()
- shared transition APIs where appropriate

Avoid applying animations indiscriminately.

7.4 Screen Transitions

Screen transitions must feel connected.

Recommended behavior:

- Entering screens should use subtle fade and movement.
- Exiting screens should not abruptly disappear.
- Shared elements should maintain visual continuity.
- Back navigation should feel natural.
- Modal screens should emerge smoothly.
- Transitions should remain interruptible.

Use shared-element transitions where they provide meaningful continuity.

7.5 Button Interaction

When pressed:

1. Slightly reduce scale.
2. Adjust surface brightness.
3. Apply subtle feedback.
4. Return naturally to the original state.

Suggested scale:

Default: 1.00
Pressed: 0.97–0.985
Released: 1.00

Do not make buttons excessively bounce.

7.6 Scroll Animations

Scrolling must feel natural and responsive.

Requirements:

- Smooth scrolling.
- No unnecessary animation lag.
- Subtle parallax only when appropriate.
- Navigation surfaces may respond to scroll position.
- Lazy loading for long content.
- Avoid expensive effects on every scroll frame.

7.7 Loading Animations

Loading indicators must be elegant and lightweight.

Prefer:

- Subtle rotating indicators.
- Smooth progress transitions.
- Skeleton loading.
- Animated status indicators.
- Contextual loading feedback.

Avoid:

- Flashing elements.
- Aggressive spinning.
- Fake progress percentages.
- Unnecessary decorative loading screens.

7.8 Micro Interactions

Small interactions should make the interface feel responsive.

Examples:

- Toggle switches transition smoothly.
- Icons morph subtly.
- Selection indicators move naturally.
- Checkmarks appear with a soft scale transition.
- Cards respond to touch.
- Expandable sections animate their content.
- Status indicators transition without flickering.

Every animation must have a purpose.

---

08. BACKGROUND & DEPTH

Use layered backgrounds to create visual depth.

Possible techniques:

- Subtle radial gradients.
- Soft ambient lighting.
- Low-contrast blurred shapes.
- Background image layering.
- Translucent surfaces.

Avoid:

- Excessive colorful blobs.
- Constant moving backgrounds.
- Distracting particle effects.
- Overly strong blur.
- Excessive background decoration.

The background should support the content, not compete with it.

---

09. ICONOGRAPHY

Use a consistent icon library.

Recommended:

- Material Symbols.
- Lucide Icons.
- Custom vector icons.

Icon requirements:

- Consistent stroke weight.
- Consistent sizing.
- Proper alignment.
- Clear visual meaning.
- Smooth state transitions.

Recommended icon sizes:

16dp = Small indicators
20dp = Standard actions
24dp = Navigation
28dp = Important actions
32dp = Feature icons

Avoid mixing different icon styles.

Do not use emoji as primary interface icons.

---

10. ANDROID NATIVE EXPERIENCE

The application must feel native to Android despite its iOS-inspired visual language.

Requirements:

- Proper Android system bar integration.
- Edge-to-edge layout.
- Correct system insets.
- Native keyboard integration.
- Predictive back gesture compatibility.
- Proper permission handling.
- Native accessibility support.
- Touch feedback.
- Adaptive screen layouts.
- Correct lifecycle handling.

Recommended Implementation

For Kotlin native applications:

- Kotlin.
- Jetpack Compose.
- Material 3 as an underlying component foundation.
- Custom Compose design system.
- Native Android animation APIs.
- Coroutines for asynchronous work.

Material 3 components may be customized to match the visual direction.

Do not allow default Material styling to dominate the final interface.

Blur Performance

Real-time blur can be expensive.

Implementation rules:

- Use native blur where supported.
- Provide a fallback for unsupported Android versions.
- Avoid stacking multiple expensive blur layers.
- Prefer static translucent surfaces when appropriate.
- Avoid unnecessary blur recalculation.
- Test on low-end and mid-range devices.

The visual experience must remain attractive even without real-time blur.

---

11. RESPONSIVE DESIGN

The application must adapt to different Android devices.

Support:

- Small screens.
- Standard smartphones.
- Large smartphones.
- Foldable devices where applicable.
- Different display densities.
- Portrait and landscape when supported.

Requirements:

- No clipped content.
- No overlapping elements.
- No fixed layouts that break on smaller screens.
- Proper scrolling.
- Adaptive spacing.
- Correct keyboard and system inset behavior.

Do not design exclusively for one device resolution.

---

12. PERFORMANCE REQUIREMENTS

Premium visuals must not come at the expense of performance.

Target:

- Smooth 60 FPS on supported mid-range devices.
- 90/120Hz support when available.
- Minimal unnecessary recomposition.
- Efficient memory usage.
- No excessive background processing.
- No unnecessary rendering layers.
- No UI thread blocking.

Performance Rules

1. Keep expensive rendering operations minimal.
2. Use lazy lists for large collections.
3. Optimize image sizes and formats.
4. Avoid excessive nested composables.
5. Use stable state management.
6. Avoid unnecessary recompositions.
7. Cancel animations and tasks when appropriate.
8. Avoid continuously running animations without purpose.
9. Test memory consumption.
10. Profile performance on real Android devices.

Never sacrifice application responsiveness for decorative effects.

---

13. ACCESSIBILITY

The design must remain usable by everyone.

Requirements:

- Sufficient contrast.
- Scalable text.
- Accessible touch targets.
- Screen reader descriptions.
- Semantic component labels.
- Clear focus indicators.
- Reduced-motion support.

Reduced Motion

When the user enables reduced motion:

- Reduce large movement.
- Disable unnecessary parallax.
- Reduce spring overshoot.
- Prefer short fade transitions.
- Preserve essential feedback.

Accessibility is part of the design system, not an optional feature.

---

14. DARK & LIGHT MODE

Support both themes if the application requires them.

Dark Mode

- AMOLED-friendly background.
- Layered charcoal surfaces.
- Subtle glass transparency.
- Soft highlights.
- Clear foreground contrast.

Light Mode

- Warm neutral backgrounds.
- Light translucent materials.
- Delicate shadows.
- Subtle gray borders.
- Dark readable typography.

Do not simply invert colors.

Each theme must be visually balanced independently.

---

15. DESIGN CONSISTENCY

All application screens must follow the same visual language.

Create reusable components for:

- Glass surfaces.
- Navigation.
- Buttons.
- Inputs.
- Dialogs.
- Bottom sheets.
- Cards.
- Loading indicators.
- Empty states.
- Error states.
- Status indicators.

Maintain centralized:

- Color tokens.
- Typography tokens.
- Spacing tokens.
- Corner radius tokens.
- Elevation tokens.
- Motion tokens.

Never create unrelated visual styles for individual screens.

---

16. PROHIBITED DESIGN PATTERNS

The following are strictly discouraged:

- Generic template UI.
- Excessive glassmorphism.
- Excessive neon colors.
- Random gradients.
- Inconsistent spacing.
- Inconsistent corner radii.
- Unnecessary decorative elements.
- Excessive floating cards.
- Robotic animations.
- Abrupt screen transitions.
- Excessive bounce effects.
- Cheap-looking shadows.
- Emoji-based navigation icons.
- Unreadable translucent text.
- Overly complicated layouts.
- Fake interactive elements.
- Static controls that look clickable.
- Unnecessary animations that consume resources.

---

17. FINAL QUALITY STANDARD

Before considering the interface complete, verify the following:

Visual Quality

- [ ] Consistent Liquid Glass appearance.
- [ ] Proper visual hierarchy.
- [ ] Balanced color palette.
- [ ] Professional typography.
- [ ] Clean spacing.
- [ ] Realistic glass depth.
- [ ] No unnecessary decoration.

Motion Quality

- [ ] Natural spring animations.
- [ ] Smooth screen transitions.
- [ ] Responsive button feedback.
- [ ] Natural scrolling.
- [ ] No stiff movement.
- [ ] No excessive bounce.
- [ ] No unnecessary delays.

Android Quality

- [ ] Proper system insets.
- [ ] Native keyboard behavior.
- [ ] Predictive back compatibility.
- [ ] Responsive layouts.
- [ ] Accessible controls.
- [ ] Correct lifecycle behavior.

Performance Quality

- [ ] No noticeable UI stuttering.
- [ ] No excessive blur rendering.
- [ ] Efficient memory usage.
- [ ] Stable scrolling.
- [ ] No unnecessary background animations.
- [ ] Smooth performance on mid-range devices.

---

18. FINAL INSTRUCTION TO AI AGENT

Treat this DESIGN.md as the primary visual and interaction specification for the entire application.

Do not interpret this document as a suggestion to add a few transparent cards or rounded buttons.

The objective is to create a complete, cohesive, premium Liquid Glass experience.

Every screen must feel intentionally designed.

Every interaction must feel natural.

Every animation must have fluid motion and appropriate timing.

Every visual effect must serve a purpose.

Prioritize:

1. Visual consistency.
2. Natural motion.
3. Interaction quality.
4. Native Android usability.
5. Performance.
6. Accessibility.

Do not use placeholder-level UI implementations.

Do not leave screens using default framework styling when they should follow the custom design system.

Do not sacrifice functionality for appearance.

FINAL DESIGN GOAL:

Build an Android application that feels like a premium next-generation product, combining the elegance of iOS Liquid Glass with the flexibility, responsiveness, and performance of native Android.

It should feel fluid, immersive, refined, and effortlessly modern.

Not just beautiful UI. A complete premium user experience.