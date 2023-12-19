import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { TestHelper } from "src/app/test/test-helper";

import { LoginFormComponent } from "./login-form.component";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";

describe("LoginFormComponent", () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;
  let testHelper: TestHelper<LoginFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginFormComponent],
      imports: [
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    testHelper = new TestHelper(fixture);
    fixture.detectChanges();
  });

  it("should emit username and password", () => {
    let username: string;
    let password: string;

    component.login.subscribe((event) => {
      username = event.username;
      password = event.password;
    });

    writeUsername("username");
    writePassword("pwd");

    clickLoginButton();

    expect(username!).toBe("username");
    expect(password!).toBe("pwd");
    expect(component.loginForm.valid).toBe(true);
  });

  it("should not emit if username and password are both empty", () => {
    let username: string;
    let password: string;

    component.login.subscribe((event) => {
      username = event.username;
      password = event.password;
    });

    clickLoginButton();

    expect(username!).toBeUndefined();
    expect(password!).toBeUndefined();
    expect(component.loginForm.valid).toBe(false);
  });

  it("should not emit if username is empty", () => {
    let username: string;
    let password: string;

    component.login.subscribe((event) => {
      username = event.username;
      password = event.password;
    });

    writePassword("pwd");

    clickLoginButton();

    expect(username!).toBeUndefined();
    expect(password!).toBeUndefined();
    expect(component.loginForm.valid).toBe(false);
  });

  it("should not emit if password is empty", () => {
    let username: string;
    let password: string;

    component.login.subscribe((event) => {
      username = event.username;
      password = event.password;
    });

    writeUsername("username");

    clickLoginButton();

    expect(username!).toBeUndefined();
    expect(password!).toBeUndefined();
    expect(component.loginForm.valid).toBe(false);
  });

  it("should not show error message if username and password are present", () => {
    writeUsername("username");
    writePassword("pwd");

    clickLoginButton();

    fixture.detectChanges();

    const usernameErrorMessage = testHelper.getElement("username-error");
    expect(usernameErrorMessage).toBeUndefined();
    const passwordErrorMessage = testHelper.getElement("password-error");
    expect(passwordErrorMessage).toBeUndefined();
  });

  it("should show error message if username is missing", () => {
    writePassword("pwd");

    clickLoginButton();

    fixture.detectChanges();

    const errorMessage = testHelper.getElement("username-error");
    expect(errorMessage).toBeDefined();
  });

  it("should show error message if password is missing", () => {
    writeUsername("username");

    clickLoginButton();

    fixture.detectChanges();

    const errorMessage = testHelper.getElement("password-error");
    expect(errorMessage).toBeDefined();
  });

  function writeUsername(username: string) {
    const usernameInput = testHelper.getInput("username-input");
    testHelper.writeInInput(usernameInput, username);
  }

  function writePassword(password: string) {
    const passwordInput = testHelper.getInput("password-input");
    testHelper.writeInInput(passwordInput, password);
  }

  function clickLoginButton() {
    const button = testHelper.getButton("login-button");
    button.click();
  }
});
