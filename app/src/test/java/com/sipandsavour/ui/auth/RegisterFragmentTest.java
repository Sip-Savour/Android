package com.sipandsavour.ui.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.view.View;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.sipandsavour.R;
import com.sipandsavour.data.Repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 34)
public class RegisterFragmentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock NavController mockNavController;

    private MockedStatic<NavHostFragment> mockedNavHost;
    private MockedStatic<Repository> mockedRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock de la navigation
        mockedNavHost = mockStatic(NavHostFragment.class);
        mockedNavHost.when(() -> NavHostFragment.findNavController(any(Fragment.class)))
                .thenReturn(mockNavController);

        // Mock du Repository pour éviter les crashs
        mockedRepository = mockStatic(Repository.class);
    }

    @After
    public void tearDown() {
        mockedNavHost.close();
        mockedRepository.close();
    }

    @Test
    public void validation_emptyFields_showsErrors() {
        FragmentScenario<RegisterFragment> scenario = FragmentScenario.launchInContainer(
                RegisterFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            // Action : Clic sur Register sans rien remplir
            fragment.getView().findViewById(R.id.btnRegister).performClick();

            // Vérifications des messages d'erreur
            TextInputLayout tilName = fragment.getView().findViewById(R.id.tilName);
            TextInputLayout tilEmail = fragment.getView().findViewById(R.id.tilEmail);
            TextInputLayout tilPassword = fragment.getView().findViewById(R.id.tilPassword);
            TextInputLayout tilDob = fragment.getView().findViewById(R.id.tilDob);

            assertNotNull(tilName.getError());
            assertNotNull(tilEmail.getError());
            assertNotNull(tilPassword.getError());
            assertNotNull(tilDob.getError());
        });
    }

    @Test
    public void validation_passwordTooShort_showsSpecificError() {
        FragmentScenario<RegisterFragment> scenario = FragmentScenario.launchInContainer(
                RegisterFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            // Remplissage d'un mot de passe trop court
            com.google.android.material.textfield.TextInputEditText etPassword = fragment.getView().findViewById(R.id.etPassword);
            etPassword.setText("123");

            fragment.getView().findViewById(R.id.btnRegister).performClick();

            TextInputLayout tilPassword = fragment.getView().findViewById(R.id.tilPassword);
            assertEquals(fragment.getString(R.string.validation_password_short), tilPassword.getError());
        });
    }

    @Test
    public void clickGoToLogin_navigatesUp() {
        FragmentScenario<RegisterFragment> scenario = FragmentScenario.launchInContainer(
                RegisterFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            // Action : Clic sur le bouton de retour au login
            fragment.getView().findViewById(R.id.btnGoToLogin).performClick();

            // Vérification : Doit appeler navigateUp()
            verify(mockNavController).navigateUp();
        });
    }

    @Test
    public void registerSuccess_navigatesToHome() {
        FragmentScenario<RegisterFragment> scenario = FragmentScenario.launchInContainer(
                RegisterFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            // On récupère le VM de l'activité pour déclencher le succès
            AuthViewModel activityVm = new ViewModelProvider(fragment.requireActivity()).get(AuthViewModel.class);

            // On simule le succès dans le LiveData
            MutableLiveData<Boolean> successData = (MutableLiveData<Boolean>) activityVm.getRegisterSuccess();
            successData.setValue(true);
        });

        // Vérification de la navigation
        verify(mockNavController).navigate(R.id.action_register_to_home);
    }

    @Test
    public void loadingState_updatesUI() {
        FragmentScenario<RegisterFragment> scenario = FragmentScenario.launchInContainer(
                RegisterFragment.class, null, R.style.Theme_SipSavour, (FragmentFactory) null);

        scenario.onFragment(fragment -> {
            AuthViewModel activityVm = new ViewModelProvider(fragment.requireActivity()).get(AuthViewModel.class);
            MaterialButton btn = fragment.getView().findViewById(R.id.btnRegister);
            View progress = fragment.getView().findViewById(R.id.progressRegister);

            // Simule le chargement
            ((MutableLiveData<Boolean>) activityVm.getIsLoading()).setValue(true);
            assertFalse(btn.isEnabled());
            assertEquals(View.VISIBLE, progress.getVisibility());

            // Simule la fin du chargement
            ((MutableLiveData<Boolean>) activityVm.getIsLoading()).setValue(false);
            assertTrue(btn.isEnabled());
            assertEquals(View.GONE, progress.getVisibility());
        });
    }
}