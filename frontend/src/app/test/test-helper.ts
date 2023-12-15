import { ComponentFixture } from "@angular/core/testing";
import { By } from "@angular/platform-browser";

/**
 * Classe de test qui aide à aller chercher des éléments HTML de la page.
 *
 * Elle utilise l'attribut `data-testid=` pour sélectionner les éléments.
 * Par exemple:
 *      <input
 *        name="username"
 *        type="text"
 *        formControlName="username"
 *        data-testid="username-input"
 *      />
 */
export class TestHelper<T> {
  constructor(private fixture: ComponentFixture<T>) {}

  getInput(testid: string): HTMLInputElement {
    return this.fixture.debugElement.query(
      By.css(`input[data-testid="${testid}"]`)
    ).nativeElement;
  }

  getElement(testid: string) {
    return this.fixture.debugElement.query(By.css(`[data-testid="${testid}"]`))
      ?.nativeElement;
  }

  getElements(testid: string) {
    return this.fixture.debugElement
      .queryAll(By.css(`[data-testid="${testid}"]`))
      .map((d) => d.nativeElement);
  }

  getButton(testid: string): HTMLButtonElement {
    return this.fixture.debugElement.query(
      By.css(`button[data-testid="${testid}"]`)
    ).nativeElement;
  }

  writeInInput(input: any, text: string) {
    input.value = text;
    input.dispatchEvent(new Event("input"));
  }
}
