# Design Document: FinanceFlow (Budget & Expense Tracker)

This document outlines the architecture, layout, styling, and navigation for **FinanceFlow**, a premium personal finance tracking application. The application is built using modern **Jetpack Compose** and **Material Design 3 (M3)** to fulfill university project requirements with a substantially complete, highly polished, and visually stunning interactive prototype.

---

## 1. Application Concept & Pages

**FinanceFlow** helps users set a monthly budget, record expenses under various categories (Food, Transportation, Shopping, Entertainment, etc.), and analyze their spending habits through interactive custom progress gauges and charts.

The application features 4 core user flows and screens, easily navigable via a sleek **Bottom Navigation Bar**:

### Core Pages (Screens)
1. **Dashboard Screen (Home)**:
   - **Visual Budget Progress Card**: A beautiful, glowing gradient card displaying:
     - Total Monthly Budget (e.g., `$1,200`)
     - Total Spent so far (with color changes if getting close to or exceeding the limit)
     - Remaining Balance (e.g., `$450` green, or red if over budget)
     - A sleek linear progress indicator.
   - **Recent Transactions Ledger**: A clean feed of the 3 most recent transactions with colorful category-specific icons.
   - **Quick Actions**: Prominent buttons to "Log Expense" or "Set Budget".

2. **Transactions Ledger Screen**:
   - **Category Filtering**: A scrollable row of selectable Filter Chips (e.g., *All*, *Food*, *Bills*, *Transport*, *Shopping*, *Entertainment*).
   - **Search & Sort**: Real-time search bar to filter transactions by description or note.
   - **Full Expense List**: A styled list of all transactions with interactive elements (e.g., a "Delete" button that instantly recalculates the entire application's budget state).

3. **Analytics & Budget Limits Screen**:
   - **Category Spending Bars**: A beautifully styled, procedurally drawn horizontal progress graph for each category, showing how much has been spent vs. the budget limit for that category.
   - **Insights Card**: Smart textual advice based on spending (e.g., "Your highest spending category is Shopping. Consider setting a limit!").

4. **Settings & Profile Screen**:
   - **Set Overall Monthly Budget**: An interactive input field to update the total budget limit, updating the main dashboard immediately.
   - **Mock Sync/Backup**: A button that simulates cloud backup with custom progress animations.
   - **User Profile**: Custom avatar selection and developer details (ideal for university project grading!).

---

## 2. Visual Design & Aesthetics

To provide a premium and professional user interface, we will customize the default application theme with a clean, modern financial color palette:

- **Primary**: Deep Forest/Emerald Green (`#0F9D58`) - represents wealth, growth, and savings.
- **Secondary**: Cool Blue (`#1A73E8`) - represents security, reliability, and planning.
- **Error/Accent**: Crimson Red (`#D93025`) - representing expenses and budget overruns.
- **Background**: Soft Tinted White (`#F8F9FA`) with dark-mode friendly surfaces (`#FFFFFF`).
- **Typography**: Clean, professional sans-serif typography utilizing Material 3's `titleLarge`, `headlineMedium` (for currency displays), and `bodyMedium` scales.
- **Shapes & Shadows**: Rounded corners (16dp to 24dp) for cards, card borders, and custom category badges.

---

## 3. Architecture & Navigation

To ensure maximum reliability, rapid compilation, and zero build-sync issues, we will implement a robust **State-Driven Architecture** with a centralized state controller. This keeps the codebase highly cohesive, readable for graders, and perfectly stable without relying on external routing libraries.

### Application State (`FinanceAppState`)
A state class that acts as the single source of truth:
- `currentScreen`: Tracks the active bottom navigation tab.
- `totalBudget`: User-configurable monthly limit (default: `$1,500`).
- `transactions`: A reactive list of expense items (with pre-populated mock data).
- `showAddDialog`: State to trigger a beautiful overlay form for creating new expenses.

---

## 4. Implementation Checklist & Phase Plan

- [x] **Phase 1: Design Specification**: Create the architectural plan and secure approval.
- [ ] **Phase 2: Domain Models & Core State**: Create data classes (`Transaction`, `Category`) and the `FinanceAppState` controller.
- [ ] **Phase 3: Base Layout & Theme Configuration**: Implement the `Scaffold`, custom custom green/blue colors, and the `NavigationBar`.
- [ ] **Phase 4: Dashboard Screen**: Implement the budget indicator card, dynamic progress calculations, and recent transactions feed.
- [ ] **Phase 5: Transactions Ledger Screen**: Implement category filtering chips, searching, list display, and deletion handlers.
- [ ] **Phase 6: Add Transaction Overlay**: Build a beautiful modal bottom sheet or dialog form with text-validation, numerical keypad, and category-dropdown support.
- [ ] **Phase 7: Analytics & Settings Screens**: Build visual progress charts, dynamic financial tips, and editable budget settings.
- [ ] **Phase 8: Verification & Unit Tests**: Implement tests checking budget additions, deletions, filtering, and total calculations to ensure perfect technical integrity.
