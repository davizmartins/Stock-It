const apiUrl = "";

// LOGIN
document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const email = document.getElementById("loginEmail").value.trim();
  const senha = document.getElementById("loginSenha").value.trim();

  if (!email || !senha) {
    Swal.fire({ icon: "warning", title: "Preencha todos os campos" });
    return;
  }

  try {
    const res = await fetch(`${apiUrl}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha })
    });

    if (res.status === 200) {
      sessionStorage.setItem("user", JSON.stringify({ email }));
      window.location.href = "../home-page/home.html";
    } else if (res.status === 401) {
      Swal.fire({ icon: "error", title: "Credenciais inválidas" });
    } else {
      Swal.fire({ icon: "error", title: "Erro no servidor" });
    }
  } catch {
    Swal.fire({ icon: "error", title: "Erro de conexão" });
  }
});

// CADASTRO
document.getElementById("formCadastro").addEventListener("submit", async (e) => {
  e.preventDefault();
  const nome = document.getElementById("cadNome").value.trim();
  const email = document.getElementById("cadEmail").value.trim();
  const senha = document.getElementById("cadSenha").value.trim();

  if (!nome || !email || !senha) {
    Swal.fire({ icon: "warning", title: "Preencha todos os campos" });
    return;
  }

  try {
    const res = await fetch(`${apiUrl}/usuarios`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nome, email, senha })
    });

    if (res.ok) {
      Swal.fire({ icon: "success", title: "Conta criada com sucesso!" }).then(() => {
        bootstrap.Modal.getInstance(document.getElementById("modalCadastro")).hide();
        document.getElementById("formCadastro").reset();
      });
    } else {
      Swal.fire({ icon: "error", title: "Erro ao cadastrar" });
    }
  } catch {
    Swal.fire({ icon: "error", title: "Erro de conexão" });
  }
});